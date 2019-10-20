package org.sailcbi.APIServer.Api.Endpoints.Member

import java.sql.CallableStatement

import javax.inject.Inject
import org.sailcbi.APIServer.Api.{AuthenticatedRequest, ResultError, ValidationResult}
import org.sailcbi.APIServer.CbiUtil.{CriticalError, Failed, Failover, NetSuccess, ParsedRequest, Resolved, ValidationError, Warning}
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, StripeError}
import org.sailcbi.APIServer.IO.Junior.JPPortal
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.{GetCartDetailsForOrderId, GetCartDetailsForOrderIdResult, GetCurrentOnlineClose}
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedProcedureCall, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.libs.json.{JsNumber, JsObject}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class SubmitPayment @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends AuthenticatedRequest {
	def post()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		val parsedRequest = ParsedRequest(request)
		val logger = PA.logger
		try {
			val rc: RequestCache = PA.getRequestCacheMember(None, parsedRequest)._2.get
			val pb: PersistenceBroker = rc.pb

			val personId = MemberUserType.getAuthedPersonId(rc.auth.userName, pb)
			val orderId = JPPortal.getOrderId(pb, personId)

			val preflight = new PreparedProcedureCall[(Int, Option[String])](Set(MemberUserType)) {
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

			val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQueryForSelect(new GetCartDetailsForOrderId(orderId)).head

			val cardData = JPPortal.getCardData(pb, orderId).get

			val closeId = pb.executePreparedQueryForSelect(new GetCurrentOnlineClose).head.closeId

			val stripeResult: Future[Either[(String, String), NetSuccess[Charge, StripeError]]] = Failover(rc.getStripeIOController(ws).createCharge(orderDetails.priceInCents, cardData.token, orderId, closeId)) match {
				case Resolved(f) => f.map({
					case s: NetSuccess[Charge, StripeError] => {
						println("Create charge net success: " + s.successObject)
						if (s.isInstanceOf[Warning[Charge, StripeError]]) {
							println("Warning: " + s.asInstanceOf[Warning[Charge, StripeError]].e)
							logger.warning("Nonblocking warning creating stripe charge", s.asInstanceOf[Warning[Charge, StripeError]].e)
						}
						Right(s)
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

			stripeResult.map(result => {
				val parseQ = new PreparedProcedureCall[(Option[Int], Option[String])](Set(MemberUserType)) {
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
						"i_charge_total" -> orderDetails.priceInCents
					)

					override def setInParametersDouble: Map[String, Double] = Map(
						"i_total" -> JPPortal.getOrderTotal(pb, orderId)
					)

					override def setInParametersVarchar: Map[String, String] = Map(
						"i_charge_success" -> (if (result.isLeft) "N" else "Y"),
						"i_charge_id" -> (if (result.isLeft) null else result.getOrElse(null).successObject.id),
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

				parseError._2 match {
					case None => Ok(new JsObject(Map(
						"successAttemptId" -> JsNumber(parseError._1.get)
					)))
					case Some(err: String) => Ok(ResultError("process_err", err).asJsObject())
				}
			})
		} catch {
			case e: Throwable => {
				e.printStackTrace()
				Future(Ok("Internal Error."))
			}
		}

	}
}