package org.sailcbi.APIServer.Api.Endpoints

import javax.inject.Inject
import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsMember @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>
		try {
			val authResult = PA.getRequestCacheMember(None, ParsedRequest(request))
			authResult._2 match {
				case Some(rc) => Future {
					Ok(JsObject(Map("value" -> JsString(rc.auth.userName))))
				}
				case None => Future {
					Ok(ResultError.UNAUTHORIZED)
				}
			}
		} catch {
			case _: UnauthorizedAccessException => Future {
				Ok(ResultError.UNAUTHORIZED)
			}
			case _: Throwable => Future {
				Ok(ResultError.UNAUTHORIZED)
			}
		}
	}
}