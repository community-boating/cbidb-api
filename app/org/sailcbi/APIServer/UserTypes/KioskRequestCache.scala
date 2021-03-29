package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import com.coleji.framework.Util.PropertiesWrapper
import org.sailcbi.APIServer.Server.CBIBootLoaderLive

class KioskRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway)
extends LockedRequestCache(userName, serverParams, dbGateway) {
	override def companion: RequestCacheObject[KioskRequestCache] = KioskRequestCache
}

object KioskRequestCache extends RequestCacheObject[KioskRequestCache] {
	val uniqueUserName = "KIOSK"

	override val requireCORSPass: Boolean = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): KioskRequestCache =
		new KioskRequestCache(userName, serverParams, dbGateway)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): KioskRequestCache = create(uniqueUserName, serverParams, dbGateway)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] =
		if (request.headers.get("Am-CBI-Kiosk").contains(customParams.getString(CBIBootLoaderLive.PROPERTY_NAMES.KIOSK_TOKEN))) Some(uniqueUserName)
		else None
}
