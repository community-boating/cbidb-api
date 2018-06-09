package Services.Authentication

import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{Cookies, Headers}

object SymonUserType extends UserType {
  val uniqueUserName = "SYMON"

  def getAuthenticatedUsernameInRequest(requestHeaders: Headers, requestCookies: Cookies, rootCB: CacheBroker, apexToken: String): Option[String] = {
    val headers = requestHeaders.toMap
    try {
      val host: String = headers("symon-host").mkString("")
      val program = headers("symon-program").mkString("")
      val argString = headers("symon-argString").mkString("")
      val status = headers("symon-status").mkString("").toInt
      val mac = headers("symon-mac").mkString("")
      val candidateHash = headers("symon-hash").mkString("")
      val isValid = PermissionsAuthority.validateSymonHash(
        host = host,
        program = program,
        argString = argString,
        status = status,
        mac = mac,
        candidateHash = candidateHash
      )
      if (isValid) Some(uniqueUserName)
      else None
    } catch {
      case _: Throwable => None
    }
  }

  def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
