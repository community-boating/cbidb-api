package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services._

class KioskRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends NonMemberRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[KioskRequestCache] = KioskRequestCache
}

object KioskRequestCache extends RequestCacheObject[KioskRequestCache] {
	val uniqueUserName = "KIOSK"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): KioskRequestCache = new KioskRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): KioskRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		if (request.headers.get("Am-CBI-Kiosk").contains(kioskToken)) Some(uniqueUserName)
		else None

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = None
}