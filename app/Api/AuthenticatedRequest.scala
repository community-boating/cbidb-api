package Api

import Services.Authentication.{AuthenticationInstance, UserType}
import Services.{PermissionsAuthority, RequestCache}
import play.api.mvc._

trait AuthenticatedRequest {
  class RequestClass extends Controller
  protected val requestClass = new RequestClass
  protected val Ok = requestClass.Ok
  protected def Status(code: Int) = requestClass.Status(code)
  protected def getRC(ut: UserType, requestHeaders: Headers, requestCookies: Cookies): RequestCache = {
    val authResult = PermissionsAuthority.getRequestCache(ut, None, requestHeaders, requestCookies)
    authResult._2 match {
      case Some(rc) => rc
      case None => throw new Exception("Unable to generate RC of type " + ut + " for request authenticated as " + authResult._1)
    }
    /*if (ret.authenticatedUserType == ut) ret
    else {
      RequestCache.constructFromSuperiorAuth(ret, ut, "") match {
        case Some(lowerRC: RequestCache) => {
          println("@@@ Successfully downgraded authentication from " + ret.authenticatedUserType + " to " + ut)
          lowerRC
        }
        case None => throw new Exception("Unable to generate RC of type " + ut + " for request authenticated as " + ret.authenticatedUserType)
      }
    }*/
  }
}