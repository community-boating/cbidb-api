package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}

class KioskRequestCache(override val userName: String) extends NonMemberRequestCache(userName) {
	override def companion: RequestCacheObject[KioskRequestCache] = KioskRequestCache
}

object KioskRequestCache extends RequestCacheObject[KioskRequestCache] {
	val uniqueUserName = "KIOSK"

	override def create(userName: String): KioskRequestCache = new KioskRequestCache(userName)
	def create: KioskRequestCache = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		if (request.headers.get("Am-CBI-Kiosk").contains(kioskToken)) Some(uniqueUserName)
		else None

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = None
}