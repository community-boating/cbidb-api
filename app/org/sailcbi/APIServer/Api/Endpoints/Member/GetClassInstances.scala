package org.sailcbi.APIServer.Api.Endpoints.Member

import java.sql.ResultSet

import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.{ParsedRequest, Profiler}
import org.sailcbi.APIServer.IO.PreparedQueries.Member.{GetChildDataQuery, GetChildDataQueryResult, GetClassInstancesQuery}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.Authentication.MemberUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
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
