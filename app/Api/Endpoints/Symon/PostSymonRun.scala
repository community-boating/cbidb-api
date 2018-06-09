package Api.Endpoints.Symon

import Api.AuthenticatedRequest
import CbiUtil._
import Entities.JsFacades.Stripe.{Charge, StripeError}
import IO.PreparedQueries.Apex._
import IO.Stripe.StripeIOController
import Services.Authentication.{ApexUserType, SymonUserType}
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

class PostSymonRun@Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  /*def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    val logger = PermissionsAuthority.logger
    val rc = getRC(SymonUserType, req)
    val pb = rc.pb


  }*/
}


