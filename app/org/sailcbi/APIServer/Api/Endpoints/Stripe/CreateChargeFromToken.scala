package org.sailcbi.APIServer.Api.Endpoints.Stripe

import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, StripeError}
import org.sailcbi.APIServer.IO.PreparedQueries.Apex._
import org.sailcbi.APIServer.IO.Stripe.StripeIOController
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

class CreateChargeFromToken @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends AuthenticatedRequest {
	def post(): Action[AnyContent] = Action.async { r => doPost(ParsedRequest(r)) }

	def doPost(req: ParsedRequest)(implicit PA: PermissionsAuthority): Future[Result] = {
		val logger = PA.logger
		val rc = getRC(ApexUserType, req)
		val pb = rc.pb
		val stripeIOController = new StripeIOController(
			PA.stripeAPIIOMechanism.get(rc)(ws),
			PA.stripeDatabaseIOMechanism.get(rc)(pb),
			logger
		)
		val params = req.postParams
		val token: String = params("token")
		val orderId: Int = params("orderId").toInt

		Failover({
			val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQueryForSelect(new GetCartDetailsForOrderId(orderId)).head
			orderDetails
		}, (e: Throwable) => logger.error("Unknown order id " + orderId, e))
				.andThenWithCatch(orderDetails => {
					val tokenRecord: ValidateTokenInOrderResult = pb.executePreparedQueryForSelect(new ValidateTokenInOrder(orderId, token)).head
					val nextPacket = (orderDetails, tokenRecord)
					nextPacket
				}, (e: Throwable) => logger.error("Mismatched token " + token + " and orderID " + orderId, e))
				.andThenWithCatch(packet => {
					val close: GetCurrentOnlineCloseResult = pb.executePreparedQueryForSelect(new GetCurrentOnlineClose).head
					val nextPacket = (packet._1, packet._2, close)
					nextPacket
				}, (e: Throwable) => logger.error("Unable to get current online close", e))
				.andThenWithCatch(packet => {
					stripeIOController.createCharge(packet._1.priceInCents, token, orderId, packet._3.closeId)
				}, (e: Throwable) => logger.error("Unknown order id + orderID", e)) match {
			case Resolved(f) => f.map({
				case s: NetSuccess[Charge, StripeError] => {
					println("Create charge net success: " + s.successObject)
					if (s.isInstanceOf[Warning[Charge, StripeError]]) {
						println("Warning: " + s.asInstanceOf[Warning[Charge, StripeError]].e)
						logger.warning("Nonblocking warning creating stripe charge", s.asInstanceOf[Warning[Charge, StripeError]].e)
					}
					Ok(List("success", s.successObject.id, s.successObject.amount).mkString("$$"))
				}
				case v: ValidationError[Charge, StripeError] => {
					println("Create charge validation error: " + v.errorObject)
					Ok(List("failure", v.errorObject.`type`, v.errorObject.message).mkString("$$"))
				}
				case e: CriticalError[Charge, StripeError] => {
					logger.error("Create charge critical error: ", e.e)
					Ok(List("failure", "cbi-api-error", e.e.getMessage).mkString("$$"))
				}
			})
			case Failed(e) => {
				logger.error("Error creating charge", e)
				Future {
					Ok(List("failure", "cbi-api-error", e.getMessage).mkString("$$"))
				}
			}
		}
	}
}


