package Api.Endpoints.Chargify

import javax.inject.Inject

import Services.{PermissionsAuthority, RequestCache}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext

class Subscriptions @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action.async { request =>
    val rc: RequestCache = PermissionsAuthority.getRequestCache(request)
    Chargify.Request.getSubscriptions(rc, ws).map(s => {
      Ok(s).as("application/json")
    })
  }
}
