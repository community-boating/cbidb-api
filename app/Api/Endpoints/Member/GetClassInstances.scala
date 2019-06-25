package Api.Endpoints.Member

import java.sql.ResultSet

import Api.AuthenticatedRequest
import CbiUtil.{ParsedRequest, Profiler}
import IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult, GetClassInstancesQuery}
import IO.PreparedQueries.PreparedQueryForSelect
import Services.Authentication.MemberUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class GetClassInstances @Inject()(implicit val exec: ExecutionContext) extends AuthenticatedRequest {
	def get(typeId: Int, juniorId: Int): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		val maybeRC = PermissionsAuthority.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2
		if (maybeRC.isEmpty) Future {
			Ok("{\"error\": \"Unauthorized\"}")
		} else {
			val rc = maybeRC.get
			val pb = rc.pb
			val queryResult = pb.executePreparedQueryForSelect(GetClassInstancesQuery.byJunior(None, typeId, juniorId)).toArray
			Future(Ok(Json.toJson(queryResult)))
		}
	})
}
