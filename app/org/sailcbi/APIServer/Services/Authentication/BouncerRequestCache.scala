package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PermissionsAuthoritySecrets, RequestCache, RequestCacheObject}

class BouncerRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends NonMemberRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[BouncerRequestCache] = BouncerRequestCache
}

object BouncerRequestCache extends RequestCacheObject[BouncerRequestCache] {
	val uniqueUserName = "BOUNCER"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): BouncerRequestCache = new BouncerRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): BouncerRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		apexToken: String,
		kioskToken: String
	)(implicit PA: PermissionsAuthority): Option[String] =
		if (
			request.headers.get(PermissionsAuthority.BOUNCER_AUTH_HEADER).contains("true") &&
					PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: RequestCache,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}