package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import Entities.JsFacades.Stripe.{StripeError, Token}
import Services.Authentication.ApexUserType
import Services.PermissionsAuthority
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class GetTokenDetails @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def get(token: String): Action[AnyContent] = Action.async {req => {
    val rc = getRC(ApexUserType, ParsedRequest(req))
    val pb = rc.pb

    val stripeRequest: WSRequest = ws.url(PermissionsAuthority.stripeURL + "tokens/" + token)
      .withAuth(PermissionsAuthority.secrets.stripeAPIKey.get(rc), "", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = stripeRequest.get
    futureResponse.map(r => {
      println(r.json.toString())
      val tokenObject = r.json.as[Token]

      def parseAsSuccess: Try[String] = Try {
        val tokenObject = r.json.as[Token]
        List(
          "success",
          tokenObject.id,
          tokenObject.used,
          tokenObject.card.last4,
          tokenObject.card.exp_month,
          tokenObject.card.exp_year,
          tokenObject.card.address_zip
        ).mkString("$$")
      }

      def parseAsFailure: Try[String] = Try {
        val errorObject = StripeError(r.json)
        List("failure", errorObject.`type`, errorObject.message).mkString("$$")
      }

      def msgTry: Try[String] = parseAsSuccess orElse parseAsFailure

      val msg: String = msgTry.getOrElse(List("failure", "Unknown error").mkString("$$"))

      Ok(msg)
    })
  }}
}
