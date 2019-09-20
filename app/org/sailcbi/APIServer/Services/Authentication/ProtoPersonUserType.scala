package org.sailcbi.APIServer.Services.Authentication

import java.sql.ResultSet

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import org.sailcbi.APIServer.Storable.{EntityVisibility, StorableClass, StorableObject}


object ProtoPersonUserType extends NonMemberUserType {
	val COOKIE_NAME = "CBIDB_PROTO"
	val COOKIE_VALUE_PREFIX = "PROTO_"
	def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		val cookies = request.cookies.filter(_.name == COOKIE_NAME)
		if (cookies.isEmpty) None
		else if (cookies.size > 1) None
		else {
			val value = cookies.toList.head.value
			if (value.startsWith(COOKIE_VALUE_PREFIX)) Some(value)
			else None
		}
	}

	def getAuthenticatedUsernameFromSuperiorAuth(
			currentAuthentication: AuthenticationInstance,
			requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

	def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

	def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY

	def getAuthedPersonId(userName: String, rootPB: PersistenceBroker): Int = {
		val ids = rootPB.executePreparedQueryForSelect(getMatchingPersonIDsQuery(userName))
		// TODO: critical error if this list has >1 element
		ids.head
	}

	def getMatchingPersonIDsQuery(userName: String): PreparedQueryForSelect[Int] = new PreparedQueryForSelect[Int](Set(ProtoPersonUserType)) {
		override def getQuery: String =
			"""
			  |select p.person_id from persons p, (
			  |    select person_id from persons minus select person_id from persons_to_delete
			  | ) ilv where p.person_id = ilv.person_id and PROTOPERSON_COOKIE = ?
				""".stripMargin
		override val params: List[String] = List(userName)
		override def mapResultSetRowToCaseObject(rs: ResultSet): Int = rs.getInt(1)
	}
}
