package Services.Authentication

import CbiUtil.ParsedRequest
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}

object ApexUserType extends NonMemberUserType {
  val uniqueUserName = "APEX"
  def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String): Option[String] = {
    val headers = request.headers.toMap
    val headerKey = "apex-token"
    if (headers.contains(headerKey) && headers(headerKey).mkString("") == apexToken) Some(uniqueUserName)
    else {
      // signet?
      val signetKey = "apex-signet"
      if (
        headers.contains(signetKey) && PermissionsAuthority.validateApexSignet(Some(headers(signetKey).mkString("")))
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
