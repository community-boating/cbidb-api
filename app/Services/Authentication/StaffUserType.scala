package Services.Authentication

import java.sql.ResultSet

import CbiUtil.ParsedRequest
import IO.PreparedQueries.PreparedQueryForSelect
import Services._
import Storable.{EntityVisibility, StorableClass, StorableObject}

object StaffUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String): Option[String] = {
    val secCookies = request.cookies.filter(_.name == PermissionsAuthority.SEC_COOKIE_NAME)
    if (secCookies.isEmpty) None
    else if (secCookies.size > 1) None
    else {
      val cookie = secCookies.toList.head
      val token = cookie.value
      println(rootCB.get("dfkjdgfjkdgfjkdgf"))
      val cacheResult = rootCB.get(PermissionsAuthority.SEC_COOKIE_NAME + "_" + token)
      println(cacheResult)
      cacheResult match {
        case None => None
        case Some(s: String) => {
          val split = s.split(",")
          if (split.length != 2) None
          val userName = split(0)
          val expires = split(1)
          println("expires ")
          println(expires)
          println("and its currently ")
          println(System.currentTimeMillis())
          if (expires.toLong < System.currentTimeMillis()) None
          else Some(userName)
        }
      }
    }
  }

  def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = {
    case class Result (userName: String, pwHash: String)
    val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerUserType)) {
      override def mapResultSetRowToCaseObject(rs: ResultSet): Result = Result(rs.getString(1), rs.getString(2))

      override def getQuery: String = "select user_name, pw_hash from users where lower(user_name) = ?"

      override val params: List[String] = List(userName.toLowerCase)
    }

    val users = rootPB.executePreparedQueryForSelect(hq)

    if (users.length == 1) Some(1, users.head.pwHash)
    else None
  }

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.FULL_VISIBILITY
}
