package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}

class BouncerRequestCache(override val userName: String) extends NonMemberRequestCache(userName) {
	override def companion: RequestCacheObject[BouncerRequestCache] = BouncerRequestCache
}

object BouncerRequestCache extends RequestCacheObject[BouncerRequestCache] {
	val uniqueUserName = "BOUNCER"

	override def create(userName: String): BouncerRequestCache = new BouncerRequestCache(userName)
	def create: BouncerRequestCache = create(uniqueUserName)

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
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}