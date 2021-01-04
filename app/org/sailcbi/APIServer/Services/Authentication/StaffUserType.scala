package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedQueryForSelect
import org.sailcbi.APIServer.Services._
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.{Filter, StorableClass, StorableObject}

class StaffUserType(override val userName: String) extends NonMemberUserType(userName) {
	override def companion: UserTypeObject[StaffUserType] = StaffUserType

	override def getPwHashForUser(rootPB: PersistenceBroker[RootUserType]): Option[(Int, String)] = {
		case class Result(userName: String, pwHash: String)
		val hq = new PreparedQueryForSelect[Result](allowedUserTypes = Set(BouncerUserType)) {
			override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Result = Result(rs.getString(1), rs.getString(2))

			override def getQuery: String = "select user_name, pw_hash from users where lower(user_name) = ?"

			override val params: List[String] = List(userName.toLowerCase)
		}

		val users = rootPB.executePreparedQueryForSelect(hq)

		if (users.length == 1) Some(1, users.head.pwHash)
		else None
	}

	// Staff have unrestricted access
	override def getObjectById[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], id: Int): Option[T] =
		pb.getObjectById(obj, id)

	override def getObjectsByIds[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] =
		pb.getObjectsByIds(obj, ids, fetchSize)

	override def getObjectsByFilters[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T] =
		pb.getObjectsByFilters(obj, filters, fetchSize)

	override def getAllObjectsOfClass[T <: StorableClass, U <: UserType](userName: String, pb: PersistenceBroker[U])(obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] =
		pb.getAllObjectsOfClass(obj, fields)

	override def commitObjectToDatabase[U <: UserType](userName: String, pb: PersistenceBroker[U])(i: StorableClass): Unit =
		pb.commitObjectToDatabase(i)
}

object StaffUserType extends UserTypeObject[StaffUserType] {
	override def create(userName: String): StaffUserType = new StaffUserType(userName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		getAuthenticatedUsernameInRequestFromCookie(request, rootCB, apexToken).filter(s => !s.contains("@"))

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootUserType]) Some(RootUserType.uniqueUserName) else None
}