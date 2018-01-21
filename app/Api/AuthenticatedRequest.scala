package Api

import Services.Authentication.UserType
import Services.{PermissionsAuthority, RequestCache}
import play.api.mvc.{AnyContent, Controller, Request}

abstract class AuthenticatedRequest(ut: UserType) extends Controller {
  def getRC(request: Request[AnyContent]): RequestCache = {
    val ret = PermissionsAuthority.getRequestCache(request)
    if (ret.authenticatedUserType == ut) ret
    else {
      RequestCache.constructFromSuperiorAuth(request, ret, ut, "") match {
        case Some(lowerRC: RequestCache) => {
          println("@@@ Successfully downgraded authentication from " + ret.authenticatedUserType + " to " + ut)
          lowerRC
        }
        case None => throw new Exception("Unable to generate RC of type " + ut + " for request authenticated as " + ret.authenticatedUserType)
      }
    }
  }
}

//