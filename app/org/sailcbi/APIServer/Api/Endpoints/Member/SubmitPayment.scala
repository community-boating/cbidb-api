package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.{ServiceRequestResult, _}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, PaymentIntent, StripeError}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.GetCurrentOnlineClose
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, MemberUserType}
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import java.sql.CallableStatement
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmitPayment @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCacheMember(None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = PortalLogic.getOrderId(pb, personId)

			startChargeProcess(rc, personId, orderId).map(parseError => parseError._2 match {
				case None => Ok(new JsObject(Map(
					"successAttemptId" -> JsNumber(parseError._1.get)
				)))
				case Some(err: String) => Ok(ResultError("process_err", err).asJsObject())
			})
		})
	}

	def postApex()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ApexUserType, None, parsedRequest, rc => {
			val pb: PersistenceBroker = rc.pb

			val params = parsedRequest.postParams
			val personId = params("personId").toInt
			val orderId: Int = params("orderId").toInt

			startChargeProcess(rc, personId, orderId).map(parseError => parseError._2 match {
				case None => Ok("success")
				case Some(err: String) => Ok(err)
			})
		})
	}

	private def startChargeProcess(rc: RequestCache, personId: Int, orderId: Int)(implicit PA: PermissionsAuthority): Future[(Option[Int], Option[String])] = {
		val pb = rc.pb

		val closeId = pb.executePreparedQueryForSelect(new GetCurrentOnlineClose).head.closeId
		val orderTotalInCents = PortalLogic.getOrderTotalCents(pb, orderId)
		val isStaggered = PortalLogic.getPaymentAdditionalMonths(rc.pb, orderId) > 0

		val stripe = rc.getStripeIOController(ws)

		if (isStaggered) {
			PortalLogic.getOrCreatePaymentIntent(pb, stripe, personId, orderId, orderTotalInCents).flatMap(pi => {
				stripe.updatePaymentIntentWithTotal(pi.id, orderTotalInCents, closeId).flatMap(_ => {
					val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType, ApexUserType)) {
						override def getQuery: String =
							s"""
							   |update ORDERS_STRIPE_PAYMENT_INTENTS set
							   |AMOUNT_IN_CENTS = $orderTotalInCents,
							   |paid_close_id = $closeId
							   |where order_id = $orderId
							   |and nvl(paid, 'N') <> 'Y'
							   |""".stripMargin
					}
					pb.executePreparedQueryForUpdateOrDelete(updateQ)

					postPaymentIntent(rc, personId, orderId, closeId, orderTotalInCents)
				})
			})
		} else {
			postPaymentIntent(rc, personId, orderId, closeId, orderTotalInCents)
		}
	}

	private def postPaymentIntent(rc: RequestCache, personId: Int, orderId: Int, closeId: Int, orderTotalInCents: Int)(implicit PA: PermissionsAuthority): Future[(Option[Int], Option[String])] = {
		val pb = rc.pb
		val logger = PA.logger

		val preflight = new PreparedProcedureCall[(Int, Option[String])](Set(MemberUserType, ApexUserType)) {
			//				procedure start_stripe_trans_preflight(
			//						i_order_id in number,
			//						o_attempt_id out number,
			//						o_error_msg out varchar2
			//				);
			override def registerOutParameters: Map[String, Int] = Map(
				"o_attempt_id" -> java.sql.Types.INTEGER,
				"o_error_msg" -> java.sql.Types.VARCHAR
			)

			override def setInParametersInt: Map[String, Int] = Map(
				"i_order_id" -> orderId
			)

			override def setInParametersVarchar: Map[String, String] = Map.empty
			override def setInParametersDouble: Map[String, Double] = Map.empty
			override def getOutResults(cs: CallableStatement): (Int, Option[String]) = {
				val err = {
					val s = cs.getString("o_error_msg")
					if (cs.wasNull()) None else Some(s)
				}
				(cs.getInt("o_attempt_id"), err)
			}
			override def getQuery: String = "api_pkg.start_stripe_trans_preflight(?, ?, ?)"
		}

		val preflightResult = pb.executeProcedure(preflight)

		type ErrorCode = String
		type ErrorMessage = String
		type ChargeID = String
		val stripeResult: Future[Either[(ErrorCode, ErrorMessage), ChargeID]] = {
			println("order total cents is: " + orderTotalInCents)
			if (orderTotalInCents <= 0) {
				println("skipping charge")
				Future(Right(null))
			} else {
				doCharge(rc, personId, orderId, orderTotalInCents, closeId) match {
					case Resolved(f) => f.map({
						case s: NetSuccess[Charge, StripeError] => {
							println("Create charge net success: " + s.successObject)
							if (s.isInstanceOf[Warning[Charge, StripeError]]) {
								println("Warning: " + s.asInstanceOf[Warning[Charge, StripeError]].e)
								logger.warning("Nonblocking warning creating stripe charge", s.asInstanceOf[Warning[Charge, StripeError]].e)
							}
							Right(s.successObject.id)
						}
						case v: ValidationError[Charge, StripeError] => {
							println("Create charge validation error: " + v.errorObject)
							Left(v.errorObject.`type`, v.errorObject.message)
						}
						case e: CriticalError[Charge, StripeError] => {
							logger.error("Create charge critical error: ", e.e)
							Left("cbi-api-error", e.e.getMessage)
						}
					})
					case Failed(e) => {
						logger.error("Error creating charge", e)
						Future {
							Left("cbi-api-error", e.getMessage)
						}
					}
				}
			}
		}

		stripeResult.map(result => {
			val parseQ = new PreparedProcedureCall[(Option[Int], Option[String])](Set(MemberUserType, ApexUserType)) {
				//						procedure start_stripe_transaction_parse(
				//								i_order_id in number,
				//								i_total in number,
				//								i_attempt_id in number,
				//								i_charge_success in char,
				//								i_charge_id in varchar2,
				//								i_charge_total in number,
				//								i_error_code in varchar2,
				//								i_stripe_error_msg in varchar2,
				//								o_success_attempt_id out number,
				//								o_error_msg out varchar2
				//						) ;
				override def registerOutParameters: Map[String, Int] = Map(
					"o_success_attempt_id" -> java.sql.Types.INTEGER,
					"o_error_msg" -> java.sql.Types.VARCHAR
				)

				override def setInParametersInt: Map[String, Int] = Map(
					"i_order_id" -> orderId,
					"i_attempt_id" -> preflightResult._1,
					"i_charge_total" -> orderTotalInCents
				)

				override def setInParametersDouble: Map[String, Double] = Map(
					"i_total" -> PortalLogic.getOrderTotalDollars(pb, orderId)
				)

				override def setInParametersVarchar: Map[String, String] = Map(
					"i_charge_success" -> (if (result.isLeft) "N" else "Y"),
					"i_charge_id" -> (if (result.isLeft) null else result.getOrElse(null)),
					"i_error_code" -> (if (result.isLeft) result.swap.getOrElse(null)._1 else null),
					"i_stripe_error_msg" -> (if (result.isLeft) result.swap.getOrElse(null)._2 else null),
				)
				override def getOutResults(cs: CallableStatement): (Option[Int], Option[String]) = {
					val err = {
						val ret = cs.getString("o_error_msg")
						if (cs.wasNull()) None else Some(ret)
					}
					val successAttemptId = {
						val ret = cs.getInt("o_success_attempt_id")
						if (cs.wasNull()) None else Some(ret)
					}
					(successAttemptId, err)
				}
				override def getQuery: String = "api_pkg.start_stripe_transaction_parse(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
			}

			val parseError = pb.executeProcedure(parseQ)
			println("Final submit payment result:  " + parseError)

			parseError
		})
	}

	private def doCharge(rc: RequestCache, personId: Int, orderId: Int, orderTotalInCents: Int, closeId: Int): Failover[Future[ServiceRequestResult[Charge, StripeError]], _] = {
		val isStaggered = PortalLogic.getPaymentAdditionalMonths(rc.pb, orderId) > 0
		val stripeController = rc.getStripeIOController(ws)

		if (isStaggered) {
			Failover(PortalLogic.getOrCreatePaymentIntent(rc.pb, stripeController, personId, orderId, orderTotalInCents).flatMap(pi => {
				stripeController.confirmPaymentIntent(pi.id).map({
					case s: NetSuccess[PaymentIntent, StripeError] => {
						val updatePIQ = new PreparedQueryForUpdateOrDelete(Set(MemberUserType, ApexUserType)) {
							override val params: List[String] = List(pi.id)
							override def getQuery: String =
								s"""
								  |update ORDERS_STRIPE_PAYMENT_INTENTS
								  |set paid = 'Y'
								  |where PAYMENT_INTENT_ID = ?
								  |""".stripMargin
						}
						rc.pb.executePreparedQueryForUpdateOrDelete(updatePIQ)
						s.map(pi => pi.charges.data.head)
					}
					case v: ValidationError[_, StripeError] => v.asInstanceOf[ServiceRequestResult[Charge, StripeError]]
				})
			}))
		} else {
			val cardData = PortalLogic.getCardData(rc.pb, orderId).get
			Failover(stripeController.createCharge(orderTotalInCents, cardData.token, orderId, closeId))
		}
	}

	case class PostShapeApex(personId: Int, orderId: Int)

	object PostShapeApex {
		implicit val format = Json.format[PostShapeApex]

		def apply(v: JsValue): PostShapeApex = v.as[PostShapeApex]
	}
}
