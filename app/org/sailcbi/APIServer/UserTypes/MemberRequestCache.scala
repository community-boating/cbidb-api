package org.sailcbi.APIServer.UserTypes

import com.coleji.neptune.Core._
import com.coleji.neptune.Exception.MuteEmailException
import com.coleji.neptune.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.neptune.Storable.ResultSetWrapper
import com.coleji.neptune.Util.PropertiesWrapper
import org.sailcbi.APIServer.Entities.MagicIds
import play.api.mvc.Result
import redis.clients.jedis.JedisPool

import scala.concurrent.{ExecutionContext, Future}


class MemberRequestCache(override val userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool)
extends LockedRequestCacheWithSquareController(userName, serverParams, dbGateway, redisPool) {
	override def companion: RequestCacheObject[MemberRequestCache] = MemberRequestCache

	lazy val getAuthedPersonId: Int = {
		val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache, RootRequestCache)) {
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
			case id :: Nil => id
			case _ => throw new Exception("Unable to get authed person id for member RC")
		}
	}

	lazy val getChildrenPersonIds: List[Int] = {
		val q = new PreparedQueryForSelect[Int](Set(MemberRequestCache)) {
			override def getQuery: String =
				s"""
				   |select b from person_relationships rl
				   |where a = ${getAuthedPersonId}
				   |and rl.type_id = ${MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK}
				""".stripMargin

			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)
		}
		executePreparedQueryForSelect(q)
	}
}

object MemberRequestCache extends RequestCacheObject[MemberRequestCache] {
	override def create(userName: String, serverParams: PropertiesWrapper, dbGateway: DatabaseGateway, redisPool: JedisPool): MemberRequestCache =
		new MemberRequestCache(userName, serverParams, dbGateway, redisPool)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		customParams: PropertiesWrapper,
	)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, SEC_COOKIE_NAME_PUBLIC).filter(s => s.contains("@"))

	def withRequestCacheMemberWithJuniorId(
		parsedRequest: ParsedRequest,
		juniorId: Int,
		block: MemberRequestCache => Future[Result]
	)(implicit exec: ExecutionContext, PA: PermissionsAuthority): Future[Result] =
		PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
			if (rc.getChildrenPersonIds.contains(juniorId)) block(rc)
			else throw new MuteEmailException(s"""
				  |junior ID ${juniorId} in request does not match allowed ids
				  | for parent ${rc.getAuthedPersonId}: ${rc.getChildrenPersonIds.mkString(", ")}
				  |""".stripMargin)
		})

	def getPwHashForUser(rc: BouncerRequestCache, userName: String): Option[(String, String, String)] = {
		case class Result(userName: String, pwHashScheme: String, pwHash: String)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerRequestCache, RootRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result = Result(rs.getString(1), rs.getString(2), rs.getString(3))

			override def getQuery: String = "select email, pw_hash_scheme, pw_hash from persons where pw_hash is not null and lower(email) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rc.executePreparedQueryForSelect(hq)

		if (users.length == 1) Some(users.head.pwHashScheme, users.head.pwHash, EMPTY_NONCE)
		else None
	}
}
