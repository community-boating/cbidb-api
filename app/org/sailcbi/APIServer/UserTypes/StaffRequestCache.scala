package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import com.coleji.neptune.Core.access.Permission
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.PropertiesWrapper
import org.sailcbi.APIServer.Entities.access.CbiUserPermissionsAuthority
import redis.clients.jedis.JedisPool

class StaffRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)
extends UnlockedRequestCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[StaffRequestCache] = StaffRequestCache

	override def hasPermission(p: Permission): Boolean = {
		upa.permissions.contains(p)
	}

	lazy val upa: CbiUserPermissionsAuthority = CbiUserPermissionsAuthority.get(this, userName)._1
}

object StaffRequestCache extends RequestCacheObject[StaffRequestCache] {
	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): StaffRequestCache =
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

			override def getQuery: String = "select user_name, pw_hash_scheme, pw_hash, auth_nonce, locked, active from USERS where lower(user_name) = ?"

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

	def getPwNonceForEmail(rc: BouncerRequestCache, email: String): Option[String] = {
		case class Result(email: String, pwHashScheme: String, pwHash: String, nonce: String, locked: Boolean, active: Boolean)
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

			override def getQuery: String = "select user_name, pw_hash_scheme, pw_hash, auth_nonce, locked, active from USERS where lower(email) = ?"

			override val params: List[String] = List(email.toLowerCase)
		}

		val users = rc.executePreparedQueryForSelect(hq)

		if (users.length == 1) {
			val user = users.head
			if (user.active && !user.locked) {
				Some(user.nonce)
			} else None
		}
		else None
	}
}