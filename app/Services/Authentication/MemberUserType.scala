package Services.Authentication

import java.sql.ResultSet

import CbiUtil.ParsedRequest
import IO.PreparedQueries.PreparedQueryForSelect
import Services._
import Storable.{EntityVisibility, StorableClass, StorableObject}

object MemberUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String): Option[String] =
    getAuthenticatedUsernameInRequestFromCookie(request, rootCB, apexToken).filter(s => s.contains("@"))

  def getAuthenticatedUsernameFromSuperiorAuth(
    currentAuthentication: AuthenticationInstance,
    requiredUserName: Option[String]
  ): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = {
    case class Result (userName: String, pwHash: String)
    val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerUserType)) {
      override def mapResultSetRowToCaseObject(rs: ResultSet): Result = Result(rs.getString(1), rs.getString(2))

      override def getQuery: String = "select email, pw_hash from persons where pw_hash is not null and lower(email) = ?"

      override val params: List[String] = List(userName.toLowerCase)
    }

    val users = rootPB.executePreparedQueryForSelect(hq)

    if (users.length == 1) Some(1, users.head.pwHash)
    else None
  }

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
