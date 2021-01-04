package org.sailcbi.APIServer.Api.Endpoints.Security

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.{BouncerUserType, RootUserType}
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TestAuth @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def getRoot: Action[AnyContent] = get("root")

	def getBouncer: Action[AnyContent] = get("bouncer")

	private def get(role: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val ut = role match {
			case "root" => RootUserType
			case "bouncer" => BouncerUserType
		}
		val pr = ParsedRequest(req)
		PA.withRequestCache(ut)(None, pr, rc => {
			Future {
				Ok(if (
					(rc.auth.isInstanceOf[RootUserType] && role == "root") ||
						(rc.auth.isInstanceOf[BouncerUserType] && role == "bouncer")
				) "true" else "false")
			}
		})


	}
}
