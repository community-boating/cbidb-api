package org.sailcbi.APIServer.Api.Endpoints.Member

import javax.inject.Inject
import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.Member.GetClassInstancesQuery
import org.sailcbi.APIServer.Services.Authentication.PublicUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class GetClassInstances @Inject()(implicit val exec: ExecutionContext) extends AuthenticatedRequest {
	def junior(typeId: Int, juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		val maybeRC = PA.getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId)._2
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
