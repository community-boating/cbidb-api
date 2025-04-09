package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.API.{ResultError, ValidationResult}
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import com.coleji.neptune.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.IO.Portal.PortalLogic.getRecurringDonations
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.GetCurrentOnlineClose
import org.sailcbi.APIServer.UserTypes.{ApexRequestCache, LockedRequestCacheWithSquareController, MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.sql.CallableStatement
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

class SubmitPayment @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController {
	def postAP()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			val orderId = PortalLogic.getOrderIdAP(rc, personId)

			SubmitPayment.startChargeProcess(rc, ws, personId, orderId).map(parseError => parseError._2 match {
				case None => Ok(new JsObject(Map(
					"successAttemptId" -> JsNumber(parseError._1.get)
				)))
				case Some(err: String) => Ok(ResultError("process_err", err).asJsObject)
			})
		})
	}

	def postJP()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {


			val personId = rc.getAuthedPersonId
			val orderId = PortalLogic.getOrderIdJP(rc, personId)

			SubmitPayment.startChargeProcess(rc, ws, personId, orderId).map(parseError => parseError._2 match {
				case None => Ok(new JsObject(Map(
					"successAttemptId" -> JsNumber(parseError._1.get)
				)))
				case Some(err: String) => Ok(ResultError("process_err", err).asJsObject)
			})
		})
	}

	def postApex()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ApexRequestCache)(None, parsedRequest, rc => {
			Future(Gone)
			/*val params = parsedRequest.postParams
			val personId = params("personId").toInt
			val orderId: Int = params("orderId").toInt

			SubmitPayment.startChargeProcess(rc, ws, personId, orderId).map(parseError => parseError._2 match {
				case None => Ok("success")
				case Some(err: String) => Ok(err)
			})*/
		})

	}

	def postStandalone()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ProtoPersonRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId.get
			val program = parsedRequest.postParams("program")
			program match {
				case ORDER_NUMBER_APP_ALIAS.DONATE | ORDER_NUMBER_APP_ALIAS.GC => {
					val orderId = PortalLogic.getOrderId(rc, personId, program)
					val usePaymentIntent = PortalLogic.getUsePaymentIntentFromOrderTable(rc, orderId).getOrElse(false)

					val result = {
						if (usePaymentIntent) {
							SubmitPayment.startChargeProcess(rc, ws, personId, orderId, true)
						} else {
							Future(SubmitPayment.startStandaloneChargeProcess(rc, orderId))
						}
					}

					result.map(parseResult => parseResult._2 match {
						case None => {
							if (usePaymentIntent) {
								PortalLogic.setRecurringDonations(rc, personId, PortalLogic.getShoppingCartDonationsAsRecurringRecords(rc, orderId), false)
							}
							PortalLogic.promoteProtoPerson(rc, personId)
							val (_, _, _, realPersonId) = PortalLogic.getAuthedPersonInfo(rc, personId)
							realPersonId match {
								case Some(mergeInto) => PortalLogic.mergeRecords(rc, mergeInto = mergeInto, mergeFrom = personId)
								case None =>
							}
							Ok(new JsObject(Map(
								"successAttemptId" -> JsNumber(parseResult._1.get)
							)))
						}
						case Some(err: String) => Ok(ResultError("process_err", err).asJsObject)
					})
				}
				case _ => Future(Ok(ValidationResult.from("An internal error has occurred.").toResultError.asJsObject))
			}
		})
	}

	def postAutoDonateCreation()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			val personId = rc.getAuthedPersonId
			doAutoDonateOrder(rc, personId)
		})
	}

	def apexDoAutoDonate(personId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		PA.withRequestCache(ApexRequestCache)(None, parsedRequest, rc => {
			println("Doing auto donate order for person " + personId)
			val donationCount = getRecurringDonations(rc, personId, true).size
			if (donationCount > 0) {
				doAutoDonateOrder(rc, personId)
			} else {
				Future(Ok("nothing to do"))
			}
		})
	}

	private def doAutoDonateOrder(rc: LockedRequestCacheWithSquareController, personId: Int) = {
		val orderId = PortalLogic.getOrderId(rc, personId, MagicIds.ORDER_NUMBER_APP_ALIAS.AUTO_DONATE)
		PortalLogic.reconstituteAutoDonateOrder(rc, personId, orderId)
		SubmitPayment.startChargeProcess(rc, ws, personId, orderId, true).map(parseError => parseError._2 match {
			case None => {
				val update = new PreparedQueryForUpdateOrDelete(Set(ApexRequestCache, MemberRequestCache)) {
					override def getQuery: String = s"update persons set NEXT_RECURRING_DONATION = add_months(util_pkg.get_sysdate,1) where person_id = $personId"
				}
				rc.executePreparedQueryForUpdateOrDelete(update)
				val updatePRDs = new PreparedQueryForUpdateOrDelete(Set(ApexRequestCache, MemberRequestCache)) {
					override def getQuery: String = s"update persons_recurring_donations set embryonic = 'N' where person_id = $personId"
				}
				rc.executePreparedQueryForUpdateOrDelete(updatePRDs)

				Ok(new JsObject(Map(
					"successAttemptId" -> JsNumber(parseError._1.get)
				)))
			}
			case Some(err: String) => Ok(ResultError("process_err", err).asJsObject)
		})
	}

	case class PostShapeApex(personId: Int, orderId: Int)

	object PostShapeApex {
		implicit val format = Json.format[PostShapeApex]

		def apply(v: JsValue): PostShapeApex = v.as[PostShapeApex]
	}
}

object SubmitPayment {
	private def startStandaloneChargeProcess(rc: RequestCache, orderId: Int): (Option[Int], Option[String]) = {
		val ppc = new PreparedProcedureCall[(Option[Int], Option[String])](Set(ProtoPersonRequestCache, MemberRequestCache)) {
			//			procedure start_stripe_transaction(
			//				i_order_id in number,
			//				o_success_attempt_id out number,
			//				o_error_msg out varchar2
			//			) as

			override def setInParametersInt: Map[String, Int] = Map(
				"i_order_id" -> orderId
			)

			override def registerOutParameters: Map[String, Int] = Map(
				"o_success_attempt_id" -> java.sql.Types.INTEGER,
				"o_error_msg" -> java.sql.Types.VARCHAR
			)

			override def getOutResults(cs: CallableStatement): (Option[Int], Option[String]) = {
				val err = {
					val s = cs.getString("o_error_msg")
					if (s == "" || cs.wasNull()) None else Some(s)
				}
				val successAttemptId = {
					val ret = cs.getInt("o_success_attempt_id")
					if (cs.wasNull()) None else Some(ret)
				}
				(successAttemptId, err)
			}

			override def getQuery: String = "api_pkg.start_stripe_transaction(?, ?, ?)"
		}
		rc.executeProcedure(ppc)
	}

	private def startChargeProcess(rc: LockedRequestCacheWithSquareController, ws: WSClient, personId: Int, orderId: Int, isAutoDonate: Boolean = false)(implicit PA: PermissionsAuthority, exec: ExecutionContext): Future[(Option[Int], Option[String])] = {
		val closeId = rc.executePreparedQueryForSelect(new GetCurrentOnlineClose).head.closeId
		val orderTotalInCents = PortalLogic.getOrderTotalCents(rc, orderId)
		val isStaggered = PortalLogic.getPaymentAdditionalMonths(rc, orderId) > 0
		val usePaymentIntent = PortalLogic.getUsePaymentIntentFromOrderTable(rc, orderId).getOrElse(false)

		/*val stripe = rc.getStripeIOController(ws)

		if (isStaggered || isAutoDonate || usePaymentIntent) {
			val pm = for (
				pi <- PortalLogic.getOrCreatePaymentIntent(rc, stripe, personId, orderId, orderTotalInCents);
				_ <- stripe.updatePaymentIntentWithTotal(pi.get.id, orderTotalInCents, closeId);
				customerId <- Future(PortalLogic.getStripeCustomerId(rc, personId));
				pm <- stripe.getCustomerDefaultPaymentMethod(customerId.get);
				_ <- stripe.updatePaymentIntentWithPaymentMethod(pi.get.id, pm.asInstanceOf[NetSuccess[Option[PaymentMethod], _]].successObject.get.id)
			) yield pm

			pm.flatMap(_ => {
				val updateQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
					override def getQuery: String =
						s"""
						   |update ORDERS_STRIPE_PAYMENT_INTENTS set
						   |AMOUNT_IN_CENTS = $orderTotalInCents,
						   |paid_close_id = $closeId
						   |where order_id = $orderId
						   |and nvl(paid, 'N') <> 'Y'
						   |""".stripMargin
				}
				rc.executePreparedQueryForUpdateOrDelete(updateQ)

				postPaymentIntent(rc, ws, personId, orderId, closeId, orderTotalInCents, isAutoDonate)
			})
		} else {
			postPaymentIntent(rc, ws, personId, orderId, closeId, orderTotalInCents, isAutoDonate)
		}
		 */
		Future(Some(0), Some(""))
	}
/*
	private def postPaymentIntent(rc: LockedRequestCacheWithSquareController, ws: WSClient, personId: Int, orderId: Int, closeId: Int, orderTotalInCents: Int, isAutoDonate: Boolean)(implicit PA: PermissionsAuthority, exec: ExecutionContext): Future[(Option[Int], Option[String])] = {
		val logger = PA.logger

		val preflight = new PreparedProcedureCall[(Int, Option[String])](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
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

		val preflightResult = rc.executeProcedure(preflight)

		type ErrorCode = String
		type ErrorMessage = String
		type ChargeID = String
		val stripeResult: Future[Either[(ErrorCode, ErrorMessage), ChargeID]] = {
			println("order total cents is: " + orderTotalInCents)
			if (orderTotalInCents <= 0) {
				println("skipping charge")
				Future(Right(null))
			} else {
				doCharge(rc, ws, personId, orderId, orderTotalInCents, closeId, isAutoDonate) match {
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
			val parseQ = new PreparedProcedureCall[(Option[Int], Option[String])](Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
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
					"i_total" -> PortalLogic.getOrderTotalDollars(rc, orderId)
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

			val parseError = rc.executeProcedure(parseQ)
			println("Final submit payment result:  " + parseError)

			parseError
		})
	}

	private def doCharge(rc: LockedRequestCacheWithSquareController, ws: WSClient, personId: Int, orderId: Int, orderTotalInCents: Int, closeId: Int, isAutoDonate: Boolean)(implicit exec: ExecutionContext): Failover[Future[ServiceRequestResult[Charge, StripeError]], _] = {
		val isStaggered = PortalLogic.getPaymentAdditionalMonths(rc, orderId) > 0
		val stripeController = rc.getStripeIOController(ws)
		val usePaymentIntent = PortalLogic.getUsePaymentIntentFromOrderTable(rc, orderId).getOrElse(false)

		if (isStaggered || isAutoDonate || usePaymentIntent) {
			Failover(PortalLogic.getOrCreatePaymentIntent(rc, stripeController, personId, orderId, orderTotalInCents).flatMap(pi => {
				stripeController.confirmPaymentIntent(pi.get.id).map({
					case s: NetSuccess[PaymentIntent, StripeError] => {
						val updatePIQ = new PreparedQueryForUpdateOrDelete(Set(MemberRequestCache, ApexRequestCache, ProtoPersonRequestCache)) {
							override val params: List[String] = List(pi.get.id)
							override def getQuery: String =
								s"""
								   |update ORDERS_STRIPE_PAYMENT_INTENTS
								   |set paid = 'Y'
								   |where PAYMENT_INTENT_ID = ?
								   |""".stripMargin
						}
						rc.executePreparedQueryForUpdateOrDelete(updatePIQ)
						s.map(pi => pi.charges.data.head)
					}
					case v: ValidationError[_, StripeError] => v.asInstanceOf[ServiceRequestResult[Charge, StripeError]]
				})
			}))
		} else {
			val cardData = PortalLogic.getCardData(rc, orderId).get
			Failover(stripeController.createCharge(orderTotalInCents, cardData.token, orderId, closeId))
		}
	}

 */
}
