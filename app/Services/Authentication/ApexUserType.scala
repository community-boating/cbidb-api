package Services.Authentication

import CbiUtil.ParsedRequest
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}

object ApexUserType extends UserType {
  val uniqueUserName = "APEX"
  def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String): Option[String] = {
    val headers = request.headers.toMap
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
