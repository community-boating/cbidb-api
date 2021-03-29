package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import com.coleji.framework.Util.PropertiesWrapper

class PublicRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway)
extends LockedRequestCacheWithStripeController(userName, serverParams, dbGateway) {
	override def companion: RequestCacheObject[PublicRequestCache] = PublicRequestCache
}

object PublicRequestCache extends RequestCacheObject[PublicRequestCache] {
	val uniqueUserName = "PUBLIC"

	override val requireCORSPass: Boolean = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): PublicRequestCache =
		new PublicRequestCache(userName, serverParams, dbGateway)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): PublicRequestCache = create(uniqueUserName, serverParams, dbGateway)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] =
		Some(uniqueUserName)
}