package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.{Person, PersonRelationship}
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.StorableQuery.{QueryBuilder, TableAlias}
import org.sailcbi.APIServer.Storable.{EntityVisibility, StorableClass, StorableObject}


class MemberUserType(override val userName: String) extends UserType(userName) {
	override def companion: UserTypeObject[MemberUserType] = MemberUserType

	override def getPwHashForUser(rootPB: PersistenceBroker): Option[(Int, String)] = {
		case class Result(userName: String, pwHash: String)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerUserType, RootUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result = Result(rs.getString(1), rs.getString(2))

			override def getQuery: String = "select email, pw_hash from persons where pw_hash is not null and lower(email) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rootPB.executePreparedQueryForSelect(hq)

		if (users.length == 1) Some(1, users.head.pwHash)
		else None
	}

	def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY

	def getAuthedPersonId(pb: PersistenceBroker): Int = {
		val q = new PreparedQueryForSelect[Int](Set(MemberUserType, RootUserType)) {
			override def getQuery: String =
				"""
				  |select p.person_id from persons p, (
				  |    select person_id from persons minus select person_id from persons_to_delete
				  | ) ilv where p.person_id = ilv.person_id and lower(email) = lower(?) and pw_hash is not null order by 1 desc
				""".stripMargin
			override val params: List[String] = List(userName)
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)
		}
		val ids = pb.executePreparedQueryForSelect(q)
		// TODO: critical error if this list has >1 element
		ids.head
	}

	def getChildrenPersons(rc: RequestCache[MemberUserType], parentPersonId: Int): List[Person] = {
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

object MemberUserType extends UserTypeObject[MemberUserType] {
	override def create(userName: String): MemberUserType = new MemberUserType(userName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, apexToken).filter(s => s.contains("@"))

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootUserType]) Some(RootUserType.uniqueUserName) else None
}