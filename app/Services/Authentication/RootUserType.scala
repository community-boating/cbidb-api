package Services.Authentication

import Services.{CacheBroker, PersistenceBroker, RequestCache}
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{AnyContent, Cookies, Headers, Request}

object RootUserType extends UserType {
  def getAuthenticatedUsernameInRequest(requestHeaders: Headers, requestCookies: Cookies, rootCB: CacheBroker, apexToken: String): Option[String] =
    None

  def getAuthenticatedUsernameFromSuperiorAuth(
    rc: RequestCache,
    desiredUserName: String
  ): Option[String] = None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.FULL_VISIBILITY
}
