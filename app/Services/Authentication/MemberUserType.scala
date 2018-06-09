package Services.Authentication

import CbiUtil.ParsedRequest
import Services._
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{Cookies, Headers}

object MemberUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String): Option[String] = None

  def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
