package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.PropertiesWrapper
import redis.clients.jedis.JedisPool

class MemberMaybeRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)
extends PersonRequestBaseCache(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[MemberMaybeRequestCache] = MemberMaybeRequestCache
	override lazy val getAuthedPersonId: Option[Int] = {
		println("CALLING MM")
		val q = new PreparedQueryForSelect[Int](Set(MemberMaybeRequestCache, RootRequestCache)) {
			override def getQuery: String =
				"""
				  |select p.person_id from persons p, (
				  |    select person_id from persons minus select person_id from persons_to_delete
				  | ) ilv where p.person_id = ilv.person_id and lower(email) = lower(?) and pw_hash is not null order by 1 desc
				""".stripMargin
			override val params: List[String] = List(userName)
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)
		}
		val ids = this.executePreparedQueryForSelect(q)
		ids match {
			case id :: Nil => Some(id)
			case _ => None
		}
	}
}

object MemberMaybeRequestCache extends RequestCacheObject[MemberMaybeRequestCache] {

	override val requiresAuthedUsername = false

	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): MemberMaybeRequestCache =
		new MemberMaybeRequestCache(userName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
																									request: ParsedRequest,
																									rootCB: CacheBroker,
																									customParams: PropertiesWrapper,
																								)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, SEC_COOKIE_NAME_PUBLIC).filter(s => s.contains("@"))

	override def test(allowed: Set[RequestCacheObject[_]]): Unit = {

	}

}
