package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil.GetPostParams
import Services.Authentication.ApexUserType
import Services.PermissionsAuthority
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest(ApexUserType) {
  def post(): Action[AnyContent] = Action.async { request => {
    val rc = getRC(request)
    val params = GetPostParams(request).get
    val token: String = params("token")

    val url = "https://api.stripe.com/v1/charges"

    val stripeRequest: WSRequest = ws.url(url).withAuth(PermissionsAuthority.secrets.stripeAPIKey.get(rc), "", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = stripeRequest.post(Map(
      "amount" -> "1234",
      "currency" -> "usd",
      "source" -> token,
      "description" -> "Test1"
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

