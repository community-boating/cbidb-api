package org.sailcbi.APIServer.Api.Endpoints.Member

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.Member.JpOffseasonClasses
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetOffseasonClassInstances @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(juniorId: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId, rc => {
			val pb = rc.pb
			val result = rc.executePreparedQueryForSelect(new JpOffseasonClasses(juniorId)).head

			Future(Ok(Json.toJson(result)))
		})
	})
}