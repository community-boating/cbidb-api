package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.Api.AuthenticatedRequest
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.Staff.GetUserHasRoleQuery
import org.sailcbi.APIServer.Services.Authentication.{BouncerUserType, RootUserType, StaffUserType}
import org.sailcbi.APIServer.Services.PermissionsAuthority
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class TestAuth @Inject()(implicit exec: ExecutionContext) extends Controller {
	def getRoot: Action[AnyContent] = get("root")

	def getBouncer: Action[AnyContent] = get("bouncer")

	private def get(role: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val ut = role match {
			case "root" => RootUserType
			case "bouncer" => BouncerUserType
		}
		val pr = ParsedRequest(req)
		val result = PA.getRequestCache(ut, None, pr)

		Future {
			Ok(if (result._1.userType == ut) "true" else "false")
		}
	}
}
