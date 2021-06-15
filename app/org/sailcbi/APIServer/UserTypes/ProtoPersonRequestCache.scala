package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Util.PropertiesWrapper
import com.redis.RedisClientPool
import org.sailcbi.APIServer.Entities.MagicIds

class ProtoPersonRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool)
extends LockedRequestCacheWithStripeController(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[ProtoPersonRequestCache] = ProtoPersonRequestCache

	def getAuthedPersonId: Option[Int] = {
		val ts = this.executePreparedQueryForSelect(getMatchingPersonIDsQuery(userName))

		ts match {
			case t :: Nil => Some(t._1)
			case _ => None
		}
	}

	def isStillProto(): Boolean = {
		val ts = this.executePreparedQueryForSelect(getMatchingPersonIDsQuery(userName))

		ts match {
			case t :: Nil => t._2 != MagicIds.PERSONS_PROTO_STATE.WAS_PROTO
			case Nil => true
			case _ => false
		}
	}

	def getMatchingPersonIDsQuery(cookieValue: String): PreparedQueryForSelect[(Int, String)] = new PreparedQueryForSelect[(Int, String)](Set(ProtoPersonRequestCache)) {
		override def getQuery: String =
			"""
			  |select p.person_id, p.proto_state from persons p, (
			  |    select person_id from persons minus select person_id from persons_to_delete
			  | ) ilv where p.person_id = ilv.person_id and PROTOPERSON_COOKIE = ?
				""".stripMargin
		override val params: List[String] = List(cookieValue)
		override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): (Int, String) = (rs.getInt(1), rs.getString(2))
	}
}

object ProtoPersonRequestCache extends RequestCacheObject[ProtoPersonRequestCache] {
	val COOKIE_NAME = "CBIDB_PROTO"
	val COOKIE_VALUE_PREFIX = "PROTO_"

	override val requireCORSPass: Boolean = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: RedisClientPool): ProtoPersonRequestCache =
		new ProtoPersonRequestCache(userName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] = {
		val cookies = request.cookies.filter(_.name == COOKIE_NAME)
		if (cookies.isEmpty) None
		else if (cookies.size > 1) None
		else {
			val value = cookies.toList.head.value
			if (value.startsWith(COOKIE_VALUE_PREFIX)) Some(value)
			else None
		}
	}
}
