package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.Staff.GetUserHasRoleQuery
import org.sailcbi.APIServer.Services.Authentication._
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserHasRole @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(userName: String, role: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		PA.withRequestCache(StaffUserType)(None, ParsedRequest(req), rc => {
			val pb = rc.pb
			val result = rc.executePreparedQueryForSelect(new GetUserHasRoleQuery(userName, role)).head
			if (result) {
				Future(Ok("{\"result\": true}"))
			} else {
				Future(Ok("{\"result\": false}"))
			}
		})
	}
}
