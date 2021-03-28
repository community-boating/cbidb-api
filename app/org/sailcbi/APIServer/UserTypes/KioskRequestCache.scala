package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core.{CacheBroker, LockedRequestCache, ParsedRequest, PermissionsAuthority, RequestCacheObject}
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets

class KioskRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends LockedRequestCache(userName, secrets) {
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