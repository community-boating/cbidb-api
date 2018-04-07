package Services.Authentication

import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{Cookies, Headers}

object ApexUserType extends UserType {
  val uniqueUserName = "APEX"
  def getAuthenticatedUsernameInRequest(requestHeaders: Headers, requestCookies: Cookies, rootCB: CacheBroker, apexToken: String): Option[String] = {
    val headers = requestHeaders.toMap
    val headerKey = "apex-token"
    if (headers.contains(headerKey) && headers(headerKey).mkString("") == apexToken) Some(uniqueUserName)
    else {
      // signet?
      val signetKey = "apex-signet"
      if (
        PermissionsAuthority.apexDebugSignet.getOrElse(None).isDefined
          && headers.contains(signetKey)
          && Some(headers(signetKey).mkString("")) == PermissionsAuthority.apexDebugSignet.getOrElse(None)
      ) Some(uniqueUserName)
      else None
    }
  }

  def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
