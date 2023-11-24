package com.coleji.neptune.Core

import com.coleji.neptune.Util.PropertiesWrapper
import redis.clients.jedis.JedisPool


class RootRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)(implicit PA: PermissionsAuthority)
extends UnlockedRequestCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[RootRequestCache] = RootRequestCache
}

object RootRequestCache extends RequestCacheObject[RootRequestCache] {
	val uniqueUserName = "ROOT"
	val ROOT_AUTH_HEADER = "origin-root"

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): RootRequestCache =
		new RootRequestCache(userName, serverParams, dbGateway, redisPool)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): RootRequestCache = create(uniqueUserName, serverParams, dbGateway, redisPool)

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