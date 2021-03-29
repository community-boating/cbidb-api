package com.coleji.framework.Core

import com.coleji.framework.Util.PropertiesWrapper


class RootRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway)(implicit PA: PermissionsAuthority)
extends UnlockedRequestCache(userName, serverParams, dbGateway) {
	override def companion: RequestCacheObject[RootRequestCache] = RootRequestCache
}

object RootRequestCache extends RequestCacheObject[RootRequestCache] {
	val uniqueUserName = "ROOT"
	val ROOT_AUTH_HEADER = "origin-root"

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): RootRequestCache =
		new RootRequestCache(userName, serverParams, dbGateway)

	def create(serverParams: PropertiesWrapper, dbGateway: DatabaseGateway): RootRequestCache = create(uniqueUserName, serverParams, dbGateway)

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