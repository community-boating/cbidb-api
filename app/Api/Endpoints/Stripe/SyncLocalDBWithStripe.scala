package Api.Endpoints.Stripe

import Api.AuthenticatedRequest
import CbiUtil._
import IO.PreparedQueries.Apex.{GetCurrentOnlineClose, GetCurrentOnlineCloseResult}
import IO.Stripe.StripeIOController
import Services.Authentication.ApexUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class SyncLocalDBWithStripe @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def post(): Action[AnyContent] = Action.async {req => {
    val logger = PermissionsAuthority.logger
    val rc = getRC(ApexUserType, ParsedRequest(req))
    val pb = rc.pb

    val close: GetCurrentOnlineCloseResult = pb.executePreparedQueryForSelect(new GetCurrentOnlineClose).head

    val stripeIOController = new StripeIOController(
      PermissionsAuthority.stripeAPIIOMechanism.get(rc)(ws),
      PermissionsAuthority.stripeDatabaseIOMechanism.get(rc)(pb)
    )

    stripeIOController.updateLocalChargesFromAPIForClose(
      close.closeId,
      DateUtil.toBostonTime(close.createdOn),
      close.finalized.map(DateUtil.toBostonTime)
    )
    Future {Ok("updated.")}
  }}
}