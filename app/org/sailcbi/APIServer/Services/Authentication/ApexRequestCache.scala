package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, RequestCache, RequestCacheObject, ResultSetWrapper}

class ApexRequestCache(override val userName: String) extends NonMemberRequestCache(userName) {
	override def companion: RequestCacheObject[ApexRequestCache] = ApexRequestCache
}

object ApexRequestCache extends RequestCacheObject[ApexRequestCache] {
	val uniqueUserName = "APEX"

	override def create(userName: String): ApexRequestCache = new ApexRequestCache(userName)
	def create: ApexRequestCache = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		val headers = request.headers.toMap
		val headerKey = "apex-token"
		if (headers.contains(headerKey) && headers(headerKey).mkString("") == apexToken) Some(uniqueUserName)
		else {
			// signet?
			val signetKey = "apex-signet"
			if (
				headers.contains(signetKey) && PA.validateApexSignet(Some(headers(signetKey).mkString("")))
			) Some(uniqueUserName)
			else None
		}
	}

	def validateApexSessionKey(rc: RequestCache, userName: String, apexSession: String, apexSessionKey: String): Boolean = {
		val q = new PreparedQueryForSelect[Int](Set(BouncerRequestCache)) {
			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = 1

			override val params: List[String] = List(userName.toLowerCase(), apexSession, apexSessionKey)

			override def getQuery: String = "select 1 from session_keys where lower(username) = ? and apex_session = ? and remote_key = ?"
		}
		rc.executePreparedQueryForSelect(q).length == 1
	}

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}