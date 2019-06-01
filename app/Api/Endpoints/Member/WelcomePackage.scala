package Api.Endpoints.Member

import java.sql.ResultSet

import Api.AuthenticatedRequest
import CbiUtil.{ParsedRequest, Profiler}
import IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult, IdentifyMemberQuery}
import IO.PreparedQueries.PreparedQueryForSelect
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class WelcomePackage @Inject()(implicit val exec: ExecutionContext) extends AuthenticatedRequest {
	def get(): Action[AnyContent] = Action.async { req => {
		val profiler = new Profiler
		val logger = PermissionsAuthority.logger
		val maybeRC = getRCOptionMember(ParsedRequest(req), None)
		if (maybeRC.isEmpty) Future {
			Ok("{\"error\": \"Unauthorized\"}")
		}
		else {
			val rc = maybeRC.get
			val pb = rc.pb
			profiler.lap("about to do first query")
			val identifyMemberResult = pb.executePreparedQueryForSelect(new IdentifyMemberQuery(rc.auth.userName)).head
			val personId: Int = identifyMemberResult.personId
			profiler.lap("got person id")
			val hasEIIResponse = pb.executePreparedQueryForSelect(new PreparedQueryForSelect[Int](Set(MemberUserType)) {
				override def getQuery: String = "select 1 from eii_responses where person_id = ? and season = util_pkg.get_current_season"

				override val params: List[String] = List(personId.toString)

				override def mapResultSetRowToCaseObject(rs: ResultSet): Int = 1
			}).nonEmpty
			val childData = pb.executePreparedQueryForSelect(new GetChildDataQuery(personId))
			profiler.lap("got child data")

			val result = WelcomePackageResult(personId, rc.auth.userName, hasEIIResponse, childData)
			implicit val format = WelcomePackageResult.format
			profiler.lap("finishing welcome pkg")
			Future {
				Ok(Json.toJson(result))
			}
		}
	}
	}

	case class WelcomePackageResult(
										   parentPersonId: Int,
										   userName: String,
										   hasEIIResponse: Boolean,
										   children: List[GetChildDataQueryResult]
								   )

	object WelcomePackageResult {
		implicit val format = Json.format[WelcomePackageResult]
	}

}
