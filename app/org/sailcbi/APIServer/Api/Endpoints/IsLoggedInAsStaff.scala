package org.sailcbi.APIServer.Api.Endpoints

import org.sailcbi.APIServer.Api.ResultError
import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.StaffUserType
import org.sailcbi.APIServer.Services.PermissionsAuthority
import org.sailcbi.APIServer.Services.PermissionsAuthority.UnauthorizedAccessException
import javax.inject.Inject
import play.api.libs.json.{JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsStaff @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get(): Action[AnyContent] = Action.async { request =>
		try {
			val authResult = PermissionsAuthority.getRequestCache(StaffUserType, None, ParsedRequest(request))
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
