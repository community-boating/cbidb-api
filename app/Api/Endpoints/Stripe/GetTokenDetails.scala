package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import Services.Authentication.ApexUserType
import Services.{PermissionsAuthority, ServerStateContainer}
import Stripe.JsFacades.Token
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class GetTokenDetails @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def get(token: String): Action[AnyContent] = Action.async {req => {
    val rc = getRC(ApexUserType, req.headers, req.cookies)
    val pb = rc.pb

    val stripeRequest: WSRequest = ws.url(PermissionsAuthority.stripeURL + "tokens/" + token)
      .withAuth(PermissionsAuthority.secrets.stripeAPIKey.get(rc), "", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = stripeRequest.get
    futureResponse.map(r => {
      println(r.json.toString())
      val tokenObject = r.json.as[Token]
      println("did it")
      def parseAsSuccess: Try[String] = Try {
        println("here we go")
        val tokenObject = r.json.as[Token]
        println(tokenObject)
        println("bnbnbn")
        println(tokenObject.card.address_zip)
        List("success").mkString("$$")
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
