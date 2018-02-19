package Services.Authentication

import Services.{CacheBroker, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{Cookies, Headers}

object RootUserType extends UserType {
  val uniqueUserName = ""
  def getAuthenticatedUsernameInRequest(requestHeaders: Headers, requestCookies: Cookies, rootCB: CacheBroker, apexToken: String): Option[String] =
    None

  def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String] = None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.FULL_VISIBILITY
}
