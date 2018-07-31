package Api.Endpoints.Stripe

import Api.AuthenticatedRequest
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class SyncLocalDBWithStripe @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def post(): Action[AnyContent] = Action.async {req => {
    Future {Ok("implementation deleted.")}
  }}
}