package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, OracleBroker, PermissionsAuthority, PermissionsAuthoritySecrets, PersistenceBroker, RequestCache, RequestCacheObject}

class RootRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets)(implicit PA: PermissionsAuthority) extends NonMemberRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[RootRequestCache] = RootRequestCache
}

object RootRequestCache extends RequestCacheObject[RootRequestCache] {
	val uniqueUserName = "ROOT"

	override def create(userName: String): RootRequestCache = new RootRequestCache(userName)
	def create: RootRequestCache = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		if (
			request.headers.get(PermissionsAuthority.ROOT_AUTH_HEADER).contains("true") &&
					PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None
	}

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: RequestCache,
		requiredUserName: Option[String]
	): Option[String] = None
}