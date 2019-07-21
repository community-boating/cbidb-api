package Api.Endpoints

import Api.ResultError
import CbiUtil.ParsedRequest
import Services.Authentication.StaffUserType
import Services.PermissionsAuthority
import Services.PermissionsAuthority.UnauthorizedAccessException
import javax.inject.Inject
import play.api.libs.json.{JsArray, JsObject, JsString}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsMember @Inject()(implicit exec: ExecutionContext) extends Controller {
	def get(): Action[AnyContent] = Action.async { request =>
		try {
			val authResult = PermissionsAuthority.getRequestCacheMember(None, ParsedRequest(request))
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