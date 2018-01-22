package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil.GetPostParams
import Logic.PreparedQueries.{GetCartDetailsForOrderId, GetCartDetailsForOrderIdResult}
import Services.Authentication.ApexUserType
import Services.{PermissionsAuthority, ServerStateContainer}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest(ApexUserType) {
  def post(): Action[AnyContent] = Action.async { request => {
    // TODO: wrap in try, e.g. what if orderID doesnt exist
    val rc = getRC(request)
    val pb = rc.pb
    val params = GetPostParams(request).get
    val token: String = params("token")
    val orderId: Int = params("orderId").toInt

    // TODO: price isnt coming out correctly
    // TODO: also validate that token is associated with requested orderid
    val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQuery(new GetCartDetailsForOrderId(orderId)).head

    val url = "https://api.stripe.com/v1/charges"

    val stripeRequest: WSRequest = ws.url(url).withAuth(PermissionsAuthority.secrets.stripeAPIKey.get(rc), "", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = stripeRequest.post(Map(
      "amount" -> orderDetails.priceInCents.toString,
      "currency" -> "usd",
      "source" -> token,
      "description" -> ("Charge for orderId " + orderId + " time " + ServerStateContainer.get.nowDateTImeString)
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

      val msg: String = msgTry.getOrElse(List("failure", "Unknown error").mkString("$$"))

      Ok(msg)
    })
  }}
}

