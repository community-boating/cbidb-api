package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets

class PublicRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends LockedRequestCacheWithStripeController(userName, secrets) {
	override def companion: RequestCacheObject[PublicRequestCache] = PublicRequestCache

}

object PublicRequestCache extends RequestCacheObject[PublicRequestCache] {
	val uniqueUserName = "PUBLIC"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): PublicRequestCache = new PublicRequestCache(userName, secrets)
	def create(secrets: PermissionsAuthoritySecrets): PublicRequestCache = create(uniqueUserName)(secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		Some(uniqueUserName)

	// Anyone can downgrade from anything to public
//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = Some(uniqueUserName)
}