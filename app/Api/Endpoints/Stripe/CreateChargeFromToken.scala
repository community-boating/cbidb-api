package Api.Endpoints.Stripe

import javax.inject.Inject

import CbiUtil.GetPostParams
import Services.PermissionsAuthority
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequest, WSResponse}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends Controller {
  def post(): Action[AnyContent] = Action.async { request => {
    val params = GetPostParams(request).get
    val token: String = params("token")

    val url = "https://api.stripe.com/v1/charges"

    val stripeRequest: WSRequest = ws.url(url).withAuth(PermissionsAuthority.stripeAPIKey.get, "", WSAuthScheme.BASIC)
    val futureResponse: Future[WSResponse] = stripeRequest.post(Map(
      "amount" -> "1234",
      "currency" -> "usd",
      "source" -> token,
      "description" -> "Test1"
    ))
    futureResponse.map(r => {
      val chargeObject = Stripe.JsFacades.Charge(r.json)
      Ok(chargeObject.id + ": " + chargeObject.amount)
    })
  }}
}

