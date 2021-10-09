package com.coleji.neptune.Core

import com.coleji.neptune.Util.PropertiesWrapper
import com.redis.RedisClientPool


class RootRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool)(implicit PA: PermissionsAuthority)
extends UnlockedRequestCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[RootRequestCache] = RootRequestCache
}

object RootRequestCache extends RequestCacheObject[RootRequestCache] {
	val uniqueUserName = "ROOT"
	val ROOT_AUTH_HEADER = "origin-root"

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): RootRequestCache =
		new RootRequestCache(userName, serverParams, dbGateway, redisPool)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): RootRequestCache = create(uniqueUserName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] = {
		if (
			request.headers.get(ROOT_AUTH_HEADER).contains("true") &&
			PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None
	}
}