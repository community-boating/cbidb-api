package Api.Endpoints.Symon

import Api.AuthenticatedRequest
import CbiUtil._
import IO.PreparedQueries.Symon.StoreSymonRun
import Services.Authentication.SymonUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Result}

import scala.concurrent.{ExecutionContext, Future}

class PostSymonRun@Inject() (ws: WSClient) (implicit exec: ExecutionContext) extends AuthenticatedRequest {
  def post(): Action[AnyContent] = Action.async {r => doPost(ParsedRequest(r))}

  def doPost(req: ParsedRequest): Future[Result] = {
    val logger = PermissionsAuthority.logger
    val rc = getRC(SymonUserType, req)
    val pb = rc.pb

    pb.executePreparedQueryForInsert(new StoreSymonRun(
      req.postParams("symon-host"),
      req.postParams("symon-program"),
      req.postParams("symon-argString"),
      req.postParams("symon-status").toInt,
      req.postParams("symon-mac")
    ))

    Future{ Ok("inserted.")}
  }
}


