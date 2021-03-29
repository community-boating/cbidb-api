package org.sailcbi.APIServer.Api.Endpoints

import com.coleji.framework.API.ResultError
import com.coleji.framework.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.framework.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsMember @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		try {
			PA.withRequestCache(MemberRequestCache)(None, ParsedRequest(request), rc => {
				Future {
					Ok(JsObject(Map("value" -> JsString(rc.userName))))
				}
			})
		} catch {
			// no need to print a stacktrace or fire an email
			case _: UnauthorizedAccessException => Future {
				Ok(ResultError.UNAUTHORIZED)
			}
		}
	}
}
