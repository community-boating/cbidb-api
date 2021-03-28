package org.sailcbi.APIServer.Api.Endpoints

import com.coleji.framework.API.ResultError
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsMember @Inject()(implicit exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		try {
			PA.withRequestCacheMember(ParsedRequest(request), rc => {
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
