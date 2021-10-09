package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.IO.PreparedQueries.Staff.GetUserHasRoleQuery
import org.sailcbi.APIServer.UserTypes._
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetUserHasRole @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(userName: String, role: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {

			val result = rc.executePreparedQueryForSelect(new GetUserHasRoleQuery(userName, role)).head
			if (result) {
				Future(Ok("{\"result\": true}"))
			} else {
				Future(Ok("{\"result\": false}"))
			}
		})
	}
}
