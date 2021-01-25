package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services._

class StaffRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends NonMemberRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[StaffRequestCache] = StaffRequestCache
}

object StaffRequestCache extends RequestCacheObject[StaffRequestCache] {
	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): StaffRequestCache = new StaffRequestCache(userName, secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, apexToken).filter(s => !s.contains("@"))

	override def getPwHashForUser(rootRC: RootRequestCache, userName: String): Option[(Int, String)] = {
		case class Result(userName: String, pwHash: String)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result = Result(rs.getString(1), rs.getString(2))

			override def getQuery: String = "select user_name, pw_hash from users where lower(user_name) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rootRC.executePreparedQueryForSelect(hq)

		if (users.length == 1) Some(1, users.head.pwHash)
		else None
	}

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: RequestCache,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}