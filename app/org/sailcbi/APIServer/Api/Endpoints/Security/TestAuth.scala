package org.sailcbi.APIServer.Api.Endpoints.Security

import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority, RootRequestCache}
import org.sailcbi.APIServer.UserTypes.BouncerRequestCache
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TestAuth @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def getRoot: Action[AnyContent] = get("root")

	def getBouncer: Action[AnyContent] = get("bouncer")

	private def get(role: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { req =>
		val ut = role match {
			case "root" => RootRequestCache
			case "bouncer" => BouncerRequestCache
		}
		val pr = ParsedRequest(req)
		PA.withRequestCache(ut)(None, pr, rc => {
			Future {
				Ok(if (
					(rc.isInstanceOf[RootRequestCache] && role == "root") ||
						(rc.isInstanceOf[BouncerRequestCache] && role == "bouncer")
				) "true" else "false")
			}
		})


	}
}
