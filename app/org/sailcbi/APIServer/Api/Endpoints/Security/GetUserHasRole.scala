package org.sailcbi.APIServer.Api.Endpoints.Security

import javax.inject.Inject
import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.Staff.GetUserHasRoleQuery
import org.sailcbi.APIServer.Services.Authentication._
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.{ExecutionContext, Future}

class GetUserHasRole @Inject()(implicit exec: ExecutionContext) extends AuthenticatedRequest {
	def get(userName: String, role: String): Action[AnyContent] = Action.async { req =>
		Future {
			println("headers: " + req.headers)
			try {
				val rc = getRC(StaffUserType, ParsedRequest(req))
				val pb = rc.pb
				val result = pb.executePreparedQueryForSelect(new GetUserHasRoleQuery(userName, role)).head
				if (result) {
					Ok("{\"result\": true}")
				} else {
					Ok("{\"result\": false}")
				}
			} catch {
				case e: Exception => {
					println(e)
					Ok("{\"result\": false}")
				}
			}
		}
	}
}
