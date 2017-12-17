package Api.Endpoints

import javax.inject.Inject

import Services.Authentication.StaffUserType
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{PermissionsAuthority, RequestCache}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsStaff @Inject() (implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action.async {request =>
    try {
      val rc: RequestCache = PermissionsAuthority.spawnRequestCache(StaffUserType, request)
      Future {
        Ok(rc.authenticatedUserName)
      }
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("false") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

}
