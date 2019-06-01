package Api.Endpoints.Security

import Api.AuthenticatedRequest
import CbiUtil.ParsedRequest
import IO.PreparedQueries.Staff.GetUserHasRoleQuery
import Services.Authentication.{BouncerUserType, RootUserType, StaffUserType}
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class TestAuth @Inject()(implicit exec: ExecutionContext) extends Controller {
	def getRoot: Action[AnyContent] = get("root")

	def getBouncer: Action[AnyContent] = get("bouncer")

	private def get(role: String): Action[AnyContent] = Action.async { req =>
		val ut = role match {
			case "root" => RootUserType
			case "bouncer" => BouncerUserType
		}
		val pr = ParsedRequest(req)
		val result = PermissionsAuthority.getRequestCache(ut, None, pr)

		Future {
			Ok(if (result._1.userType == ut) "true" else "false")
		}
	}
}
