package Api.Endpoints

import javax.inject.Inject

import Services.Authentication.{PublicUserType, StaffUserType}
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{PermissionsAuthority, RequestCache}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsStaff @Inject() (implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action.async {request =>
    try {
      val authResult = PermissionsAuthority.getRequestCache(PublicUserType, None, request.headers, request.cookies)
      authResult._2 match {
        case Some(rc) => Future { Ok(rc.auth.userName) }
        case None => Future{ Ok("false") }
      }
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("false") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

}
