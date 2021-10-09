package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import com.coleji.neptune.Util.PropertiesWrapper
import com.redis.RedisClientPool

class PublicRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool)
extends LockedRequestCacheWithStripeController(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[PublicRequestCache] = PublicRequestCache
}

object PublicRequestCache extends RequestCacheObject[PublicRequestCache] {
	val uniqueUserName = "PUBLIC"

	override val requireCORSPass: Boolean = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): PublicRequestCache =
		new PublicRequestCache(userName, serverParams, dbGateway, redisPool)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): PublicRequestCache = create(uniqueUserName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] =
		Some(uniqueUserName)
}