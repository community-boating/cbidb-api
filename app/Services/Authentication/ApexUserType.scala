package Services.Authentication

import Services.{CacheBroker, PersistenceBroker, RequestCache}
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{Cookies, Headers}

object ApexUserType extends UserType {
  val uniqueUserName = "APEX"
  def getAuthenticatedUsernameInRequest(requestHeaders: Headers, requestCookies: Cookies, rootCB: CacheBroker, apexToken: String): Option[String] = {
    val headers = requestHeaders.toMap
    val headerKey = "apex-token"
    if (headers.contains(headerKey) && headers(headerKey).mkString("") == apexToken) Some(uniqueUserName)
    else None
  }

  def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String] = None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
