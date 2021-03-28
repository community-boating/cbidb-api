package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core.{CacheBroker, ParsedRequest, PermissionsAuthority, RequestCacheObject, UnlockedRequestCache}
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets

class RootRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets)(implicit PA: PermissionsAuthority) extends UnlockedRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[RootRequestCache] = RootRequestCache
}

object RootRequestCache extends RequestCacheObject[RootRequestCache] {
	val uniqueUserName = "ROOT"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): RootRequestCache = new RootRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): RootRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		if (
			request.headers.get(PermissionsAuthority.ROOT_AUTH_HEADER).contains("true") &&
					PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None
	}

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = None
}