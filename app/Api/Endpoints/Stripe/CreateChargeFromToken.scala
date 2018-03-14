package Api.Endpoints.Stripe

import javax.inject.Inject

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import IO.PreparedQueries.Apex._
import Services.Authentication.ApexUserType
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

class CreateChargeFromToken @Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    val ultimateErrMsg = "An internal error occurred.  Tech support has been notified and this issue will be resolved within 24 hours.  Please do not attempt to resubmit payment."
    try {
      val rc = getRC(ApexUserType, req)
      val pb = rc.pb
      val params = req.postParams
      val token: String = params("token")
      val orderId: Int = params("orderId").toInt

      val orderDetails: GetCartDetailsForOrderIdResult = pb.executePreparedQueryForSelect(new GetCartDetailsForOrderId(orderId)).head
      val tokenRecord: ValidateTokenInOrderResult = pb.executePreparedQueryForSelect(new ValidateTokenInOrder(orderId, token)).head
      val close: GetCurrentOnlineCloseResult = pb.executePreparedQueryForSelect(new GetCurrentOnlineClose).head


      Future{Ok("dfg")}
    } catch {
      case e: Throwable => {
        println(e)
        Future {Ok(List("failure", "cbi-api-error", e.getMessage).mkString("$$"))}
      }
    }
  }
}


