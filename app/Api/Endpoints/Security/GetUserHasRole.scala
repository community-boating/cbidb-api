package Api.Endpoints.Security

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import IO.PreparedQueries.Staff.GetUserHasRoleQuery
import Services.Authentication._
import javax.inject.Inject
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
