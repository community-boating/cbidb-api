package com.coleji.neptune.Core

import com.coleji.neptune.Exception.UserTypeMismatchException
import com.coleji.neptune.Util.PropertiesWrapper
import redis.clients.jedis.JedisPool

abstract class RequestCacheObject[T <: RequestCache] {
	val EMPTY_NONCE = "$EMPTY_AUTH_NONCE$"
	val SEC_COOKIE_NAME_PUBLIC = "CBIDB-SEC"
	val SEC_COOKIE_NAME_STAFF = "CBIDB-SEC-STAFF"

	def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): T

	def test(allowed: Set[RequestCacheObject[_]]): Unit = {
		println(allowed)
		println(this)
		if (!allowed.contains(this)) throw new UserTypeMismatchException()
	}

	val requireCORSPass: Boolean = true

	val requiresAuthedUsername: Boolean = true

	// Given a request (and an unrestricted CacheBroker), determine if the request is authenticated against this mechanism.
	// Return Some(authenticated username) if so, None otherwise
	def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String]

	// TODO: this is not a good way to separate members from staff
	def getAuthenticatedUsernameInRequestFromCookie(request: ParsedRequest, rootCB: CacheBroker, cookieName: String): Option[String] = {
		val secCookies = request.cookies.filter(_.name == cookieName)
		if (secCookies.isEmpty) None
		else if (secCookies.size > 1) None
		else {
			val cookie = secCookies.toList.head
			val token = cookie.value
			println("Found cookie on request: " + token)
			val cacheResult = rootCB.get(cookieName + "_" + token)
			println(cacheResult)
			cacheResult match {
				case None => None
				case Some(s: String) => {
					val split = s.split(",")
					if (split.length != 2) None // essentially panics if there are commas in the username
					// If the comma is the first char, userName will be ""
					val userName = split(0)
					val expires = split(1)
					println("expires ")
					println(expires)
					println("and its currently ")
					println(System.currentTimeMillis())
					if (expires.toLong < System.currentTimeMillis()) {
						println("yeah thats expired")
						None
					}
					else Some(userName)
				}
			}
		}
	}
}
