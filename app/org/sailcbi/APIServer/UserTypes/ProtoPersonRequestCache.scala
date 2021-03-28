package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets

class ProtoPersonRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends LockedRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[ProtoPersonRequestCache] = ProtoPersonRequestCache

	def getAuthedPersonId(): Option[Int] = {
		val ids = this.executePreparedQueryForSelect(getMatchingPersonIDsQuery(userName))
		// TODO: critical error if this list has >1 element
		ids.headOption
	}

	def getMatchingPersonIDsQuery(cookieValue: String): PreparedQueryForSelect[Int] = new PreparedQueryForSelect[Int](Set(ProtoPersonRequestCache)) {
		override def getQuery: String =
			"""
			  |select p.person_id from persons p, (
			  |    select person_id from persons minus select person_id from persons_to_delete
			  | ) ilv where p.person_id = ilv.person_id and PROTOPERSON_COOKIE = ?
				""".stripMargin
		override val params: List[String] = List(cookieValue)
		override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)
	}
}

object ProtoPersonRequestCache extends RequestCacheObject[ProtoPersonRequestCache] {
	val COOKIE_NAME = "CBIDB_PROTO"
	val COOKIE_VALUE_PREFIX = "PROTO_"

	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): ProtoPersonRequestCache = new ProtoPersonRequestCache(userName, secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		val cookies = request.cookies.filter(_.name == COOKIE_NAME)
		if (cookies.isEmpty) None
		else if (cookies.size > 1) None
		else {
			val value = cookies.toList.head.value
			if (value.startsWith(COOKIE_VALUE_PREFIX)) Some(value)
			else None
		}
	}

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}