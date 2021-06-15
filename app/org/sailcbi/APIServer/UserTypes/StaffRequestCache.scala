package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.PropertiesWrapper
import com.redis.RedisClientPool

class StaffRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool)
extends UnlockedRequestCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[StaffRequestCache] = StaffRequestCache
}

object StaffRequestCache extends RequestCacheObject[StaffRequestCache] {
	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): StaffRequestCache =
		new StaffRequestCache(userName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, SEC_COOKIE_NAME_STAFF).filter(s => !s.contains("@"))

	def getPwHashForUser(rc: BouncerRequestCache, userName: String): Option[(String, String, String)] = {
		case class Result(userName: String, pwHashScheme: String, pwHash: String, nonce: String, locked: Boolean, active: Boolean)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerRequestCache, RootRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result =
				Result(
					rs.getString(1),
					rs.getString(2),
					rs.getString(3),
					rs.getOptionString(4).getOrElse(EMPTY_NONCE),
					locked = rs.getOptionBooleanFromChar(5).getOrElse(false),
					active = rs.getBooleanFromChar(6)
				)

			override def getQuery: String = "select user_name, pw_hash_scheme, pw_hash, auth_nonce, locked, active from users where lower(user_name) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rc.executePreparedQueryForSelect(hq)

		if (users.length == 1) {
			val user = users.head
			if (user.active && !user.locked) {
				Some(user.pwHashScheme, user.pwHash, user.nonce)
			} else None
		}
		else None
	}
}