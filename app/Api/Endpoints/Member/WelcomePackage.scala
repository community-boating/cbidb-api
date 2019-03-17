package Api.Endpoints.Member

import Api.AuthenticatedRequest
import CbiUtil.{ParsedRequest, Profiler}
import IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult, IdentifyMemberQuery}
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class WelcomePackage @Inject() (implicit val exec: ExecutionContext) extends AuthenticatedRequest {
  def get(): Action[AnyContent] = Action.async {req => {
    val profiler = new Profiler
    val logger = PermissionsAuthority.logger
    val maybeRC = getRCOption(MemberUserType, ParsedRequest(req))
    if (maybeRC.isEmpty) Future {Ok("{\"error\": \"Unauthorized\"}") }
    else {
      val rc = maybeRC.get
      val pb = rc.pb
      profiler.lap("about to do first query")
      val identifyMemberResult = pb.executePreparedQueryForSelect(new IdentifyMemberQuery(rc.auth.userName)).head
      val personId: Int = identifyMemberResult.personId
      profiler.lap("got person id")
      val childData = pb.executePreparedQueryForSelect(new GetChildDataQuery(personId))
      println(Json.toJson(childData))
      profiler.lap("got child data")

      val result = WelcomePackageResult(personId, rc.auth.userName, childData)
      implicit val format = WelcomePackageResult.format
      profiler.lap("finishing welcome pkg")
      Future{Ok(Json.toJson(result))}
    }
  }}

  case class WelcomePackageResult (
    parentPersonId: Int,
    userName: String,
    children: List[GetChildDataQueryResult]
  )

  object WelcomePackageResult {
    implicit val format = Json.format[WelcomePackageResult]
  }
}
