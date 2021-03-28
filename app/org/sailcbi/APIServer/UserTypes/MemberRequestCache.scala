package org.sailcbi.APIServer.UserTypes

import com.coleji.framework.Core._
import com.coleji.framework.IO.PreparedQueries.PreparedQueryForSelect
import com.coleji.framework.Storable.ResultSetWrapper
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{Person, PersonRelationship}
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets


class MemberRequestCache(override val userName: String, secrets: PermissionsAuthoritySecrets) extends LockedRequestCache(userName, secrets) {
	override def companion: RequestCacheObject[MemberRequestCache] = MemberRequestCache



	def getAuthedPersonId(): Int = {
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
		// TODO: critical error if this list has >1 element
		ids.head
	}

	def getChildrenPersons(rc: MemberRequestCache, parentPersonId: Int): List[Person] = {
		val persons = TableAlias.wrapForInnerJoin(Person)
		val personRelationship = TableAlias.wrapForInnerJoin(PersonRelationship)
		object cols {
			val pr_a = PersonRelationship.fields.a.alias(personRelationship)
			val pr_b = PersonRelationship.fields.b.alias(personRelationship)
			val personId = Person.fields.personId.alias(persons)
		}

		val personFields = List(
			Person.fields.nameFirst,
			Person.fields.nameLast
		).map(_.alias(persons))

		val qb = QueryBuilder
			.from(persons)
			.innerJoin(personRelationship, cols.pr_b.wrapFilter(_.equalsField(cols.personId)))
			.where(cols.pr_a.wrapFilter(_.equalsConstant(parentPersonId)))
			.select(cols.personId :: personFields)

		rc.executeQueryBuilder(qb).map(r => Person.construct(r.ps))
	}
}

object MemberRequestCache extends RequestCacheObject[MemberRequestCache] {
	override def create(userName: String)(secrets: PermissionsAuthoritySecrets): MemberRequestCache = new MemberRequestCache(userName, secrets)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, apexToken).filter(s => s.contains("@"))

	override def getPwHashForUser(rootRC: RootRequestCache, userName: String): Option[(String, String, String)] = {
		case class Result(userName: String, pwHashScheme: String, pwHash: String)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerRequestCache, RootRequestCache)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result = Result(rs.getString(1), rs.getString(2), rs.getString(3))

			override def getQuery: String = "select email, pw_hash_scheme, pw_hash from persons where pw_hash is not null and lower(email) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rootRC.executePreparedQueryForSelect(hq)

		if (users.length == 1) Some(users.head.pwHashScheme, users.head.pwHash, EMPTY_NONCE)
		else None
	}

//	override def getAuthenticatedUsernameFromSuperiorAuth(
//		currentAuthentication: RequestCache,
//		requiredUserName: Option[String]
//	): Option[String] = if (currentAuthentication.isInstanceOf[RootRequestCache]) Some(RootRequestCache.uniqueUserName) else None
}