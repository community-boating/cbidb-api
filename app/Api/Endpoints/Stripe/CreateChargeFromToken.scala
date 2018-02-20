package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import Logic.PreparedQueries.Apex._
import Services.Authentication.ApexUserType
import Services.{PermissionsAuthority, ServerStateContainer}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    // TODO: wrap in try, e.g. what if orderID doesnt exist
    val rc = getRC(ApexUserType, req)
    val pb = rc.pb
    val params = req.postParams
    val token: String = params("token")
    val orderId: Int = params("orderId").toInt

    val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQuery(new GetCartDetailsForOrderId(orderId)).head
    val tokenRecord: ValidateTokenInOrderResult = pb.executePreparedQuery(new ValidateTokenInOrder(orderId, token)).head

    val stripeRequest: WSRequest = ws.url(PermissionsAuthority.stripeURL + "charges")
      .withAuth(PermissionsAuthority.secrets.stripeAPIKey.get(rc), "", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = stripeRequest.post(Map(
      "amount" -> orderDetails.priceInCents.toString,
      "currency" -> "usd",
      "source" -> token,
      "description" -> ("Charge for orderId " + orderId + " time " + ServerStateContainer.get.nowDateTimeString),
      "metadata[closeId]" -> "1",
      "metadata[orderId]" -> orderId.toString,
      "metadata[token]" -> token
    ))
    futureResponse.map(r => {
      println(r.json.toString())
      def parseAsSuccess: Try[String] = Try {
        val chargeObject = Stripe.JsFacades.Charge(r.json)
        List("success", chargeObject.id, chargeObject.amount).mkString("$$")
      }
      def parseAsFailure: Try[String] = Try {
        val errorObject = Stripe.JsFacades.Error(r.json)
        List("failure", errorObject.`type`, errorObject.message).mkString("$$")
      }

      def msgTry: Try[String] = parseAsSuccess orElse parseAsFailure

      val msg: String = msgTry.getOrElse(List("failure", "cbi-api-error", "An internal error occurred.  Please do not attempt to resubmit payment.").mkString("$$"))

      Ok(msg)
    })
  }
}

