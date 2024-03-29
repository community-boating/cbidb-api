package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import com.coleji.neptune.Util.PropertiesWrapper
import org.sailcbi.APIServer.Server.CBIBootLoaderLive
import redis.clients.jedis.JedisPool

class KioskRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)
extends LockedRequestCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[KioskRequestCache] = KioskRequestCache
}

object KioskRequestCache extends RequestCacheObject[KioskRequestCache] {
	val uniqueUserName = "KIOSK"

	override val requireCORSPass: Boolean = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): KioskRequestCache =
		new KioskRequestCache(userName, serverParams, dbGateway, redisPool)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): KioskRequestCache = create(uniqueUserName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] =
		if (request.headers.get("Am-CBI-Kiosk").contains(customParams.getString(CBIBootLoaderLive.PROPERTY_NAMES.KIOSK_TOKEN))) Some(uniqueUserName)
		else None
}
