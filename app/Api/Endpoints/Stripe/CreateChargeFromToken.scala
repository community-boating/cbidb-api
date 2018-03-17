package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil._
import Entities.JsFacades.Stripe.{Charge, StripeError}
import IO.PreparedQueries.Apex._
import IO.Stripe.StripeIOController
import Services.Authentication.ApexUserType
import Services.PermissionsAuthority
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    val rc = getRC(ApexUserType, req)
    val pb = rc.pb
    val stripeIOController = new StripeIOController(
      PermissionsAuthority.stripeAPIIOMechanism.get(rc)(ws),
      PermissionsAuthority.stripeDatabaseIOMechanism.get(rc)(pb)
    )
    val params = req.postParams
    val token: String = params("token")
    val orderId: Int = params("orderId").toInt

    val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQueryForSelect(new GetCartDetailsForOrderId(orderId)).head
    val tokenRecord: ValidateTokenInOrderResult = pb.executePreparedQueryForSelect(new ValidateTokenInOrder(orderId, token)).head
    val close: GetCurrentOnlineCloseResult = pb.executePreparedQueryForSelect(new GetCurrentOnlineClose).head

    stripeIOController.createCharge(orderDetails.priceInCents, token, orderId, close.closeId).map({
      case s: NetSuccess[Charge, StripeError] => Ok(List("success", s.successObject.id, s.successObject.amount).mkString("$$"))
      case v: ValidationError[Charge, StripeError] => Ok(List("failure", v.errorObject.`type`, v.errorObject.message).mkString("$$"))
      case e: CriticalError[Charge, StripeError] => Ok(List("failure", "cbi-api-error", e.e.getMessage).mkString("$$"))
    })
  }
}


