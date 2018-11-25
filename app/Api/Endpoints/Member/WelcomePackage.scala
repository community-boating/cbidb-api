package Api.Endpoints.Member

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import IO.PreparedQueries.Member.WelcomePackageQuery
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class WelcomePackage @Inject() (implicit val exec: ExecutionContext) extends AuthenticatedRequest {
  def get(): Action[AnyContent] = Action.async {req => {
    val logger = PermissionsAuthority.logger
    val maybeRC = getRCOption(MemberUserType, ParsedRequest(req))
    if (maybeRC.isEmpty) Future {Ok("{\"error\": \"Unauthorized\"}") }
    else {
      val rc = maybeRC.get
      val pb = rc.pb
      val queryResult = pb.executePreparedQueryForSelect(new WelcomePackageQuery(rc.auth.userName)).head
      val personId: Int = queryResult.personId
      Future{Ok("{\"data\": {\"personId\": " + personId + ", \"userName\": \"" + rc.auth.userName + "\"}}")}
    }
  }}
}
