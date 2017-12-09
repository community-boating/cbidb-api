package Api.Endpoints

import javax.inject.Inject

import Services.PermissionsAuthority.UnauthorizedAccessException
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker, RequestCache}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class IsLoggedInAsStaff @Inject() (implicit exec: ExecutionContext) extends Controller {
  def get(): Action[AnyContent] = Action.async {request =>
    try {
      val rc: RequestCache = PermissionsAuthority.spawnRequestCache(request)
      Future {
        Ok(rc.authenticatedUserName)
      }
    } catch {
      case _: UnauthorizedAccessException => Future{ Ok("false") }
      case _: Throwable => Future{ Ok("Internal Error") }
    }
  }

}
