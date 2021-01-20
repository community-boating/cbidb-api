package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services._

class StaffUserType(override val userName: String) extends NonMemberUserType(userName) {
	override def companion: UserTypeObject[StaffUserType] = StaffUserType

	override def getPwHashForUser(rootPB: PersistenceBroker): Option[(Int, String)] = {
		case class Result(userName: String, pwHash: String)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result = Result(rs.getString(1), rs.getString(2))

			override def getQuery: String = "select user_name, pw_hash from users where lower(user_name) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rootPB.executePreparedQueryForSelect(hq)

		if (users.length == 1) Some(1, users.head.pwHash)
		else None
	}
}

object StaffUserType extends UserTypeObject[StaffUserType] {
	override def create(userName: String): StaffUserType = new StaffUserType(userName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, apexToken).filter(s => !s.contains("@"))

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootUserType]) Some(RootUserType.uniqueUserName) else None
}