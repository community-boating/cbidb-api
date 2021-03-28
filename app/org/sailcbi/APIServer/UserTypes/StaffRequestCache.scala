package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core.{CacheBroker, ParsedRequest, PermissionsAuthority, RequestCacheObject, UnlockedRequestCache}
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets

class StaffRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends UnlockedRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[StaffRequestCache] = StaffRequestCache
}

object StaffRequestCache extends RequestCacheObject[StaffRequestCache] {
	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): StaffRequestCache = new StaffRequestCache(userName, secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, apexToken).filter(s => !s.contains("@"))

	override def getPwHashForUser(rootRC: RootRequestCache, userName: String): Option[(String, String, String)] = {
		case class Result(userName: String, pwHashScheme: String, pwHash: String, nonce: String)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerRequestCache, RootRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result =
				Result(rs.getString(1), rs.getString(2), rs.getString(3), rs.getOptionString(4).getOrElse(EMPTY_NONCE))

			override def getQuery: String = "select user_name, pw_hash_scheme, pw_hash, auth_nonce from users where lower(user_name) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rootRC.executePreparedQueryForSelect(hq)

		if (users.length == 1) Some(users.head.pwHashScheme, users.head.pwHash, users.head.nonce)
		else None
	}

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}