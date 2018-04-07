package Api.Endpoints.Security

import Services.Authentication.StaffUserType
import Services.PermissionsAuthority
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.{ExecutionContext, Future}

class GetPwHashForUser @Inject()(implicit exec: ExecutionContext) extends Controller {
  def get(userName: String): Action[AnyContent] = Action.async {request => Future {
    println("headers: " + request.headers)
    PermissionsAuthority.getPwHashForUser(request, userName, StaffUserType) match {
      case None => Ok("NO DATA")
      // Int is the hashing scheme ID, string is the hash itself
      case Some(t: (Int, String)) => Ok(t._1 + "," + t._2)
    }
  }}
}
