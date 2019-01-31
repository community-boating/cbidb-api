package Api.Endpoints.Member

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult, IdentifyMemberQuery}
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.json.Json
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

      val identifyMemberResult = pb.executePreparedQueryForSelect(new IdentifyMemberQuery(rc.auth.userName)).head
      val personId: Int = identifyMemberResult.personId

      val childData = pb.executePreparedQueryForSelect(new GetChildDataQuery(personId))
      println(Json.toJson(childData))

      val result = WelcomePackageResult(personId, rc.auth.userName, childData)
      implicit val format = WelcomePackageResult.format

      Future{Ok("{\"data\": " + Json.toJson(result) + "}")}
    }
  }}

  case class WelcomePackageResult (
    personId: Int,
    userName: String,
    children: List[GetChildDataQueryResult]
  )

  object WelcomePackageResult {
    implicit val format = Json.format[WelcomePackageResult]
  }
}
