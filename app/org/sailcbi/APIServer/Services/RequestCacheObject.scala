package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.Authentication.RootRequestCache
import org.sailcbi.APIServer.Services.Exception.UserTypeMismatchException

abstract class RequestCacheObject[T <: RequestCache] {
	// Given a username (and an unrestricted PersistenceBroker), get the (hashingGeneration, psHash) that is active for the user
	def getPwHashForUser(rootRC: RootRequestCache, userName: String): Option[(Int, String)] = None

	def create(userName: String)(secrets: PermissionsAuthoritySecrets): T

	def test(allowed: Set[RequestCacheObject[_]]): Unit = {
		if (!allowed.contains(this)) throw new UserTypeMismatchException()
	}

	// Given a request (and an unrestricted CacheBroker), determine if the request is authenticated against this mechanism.
	// Return Some(authenticated username) if so, None otherwise
	def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		apexToken: String,
		kioskToken: String
	)(implicit PA: PermissionsAuthority): Option[String]

	protected def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: RequestCache,
		requiredUserName: Option[String]
	): Option[String]

	// If the request actually came from e.g. a Staff request, but we want to access a Member or Public endpoint,
	// use this to downgrade the request authentication
//	final def getAuthFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[RequestCache] = getAuthenticatedUsernameFromSuperiorAuth(currentAuthentication, requiredUserName).map(u => this.create(u, secrets))

	// TODO: this is not a good way to separate members from staff
	def getAuthenticatedUsernameInRequestFromCookie(request: ParsedRequest, rootCB: CacheBroker, apexToken: String): Option[String] = {
		val secCookies = request.cookies.filter(_.name == PermissionsAuthority.SEC_COOKIE_NAME)
		if (secCookies.isEmpty) None
		else if (secCookies.size > 1) None
		else {
			val cookie = secCookies.toList.head
			val token = cookie.value
			println("Found cookie on request: " + token)
			val cacheResult = rootCB.get(PermissionsAuthority.SEC_COOKIE_NAME + "_" + token)
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
