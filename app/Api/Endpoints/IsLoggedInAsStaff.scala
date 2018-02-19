package Api.Endpoints

import javax.inject.Inject

import CbiUtil.ParsedRequest
import Services.Authentication.StaffUserType
import Services.PermissionsAuthority
import Services.PermissionsAuthority.UnauthorizedAccessException
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsStaff @Inject() (implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action.async {request =>
    try {
      val authResult = PermissionsAuthority.getRequestCache(StaffUserType, None, ParsedRequest(request))
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
