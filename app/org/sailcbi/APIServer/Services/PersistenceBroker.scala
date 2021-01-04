package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.CbiUtil.TestUserType
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import org.sailcbi.APIServer.Services.Authentication.UserType
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import org.sailcbi.APIServer.Storable._

// TODO: decide on one place for all the fetchSize defaults and delete the rest
abstract class PersistenceBroker[T <: UserType] private[Services](dbConnection: DatabaseHighLevelConnection, val rc: RequestCache[T], preparedQueriesOnly: Boolean, readOnly: Boolean) {
	// All public requests need to go through user type-based security
	final private[Services] def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectByIdImplementation(obj, id)
	}

	final private[Services] def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectsByIdsImplementation(obj, ids)
	}

	final private[Services] def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectsByFiltersImplementation(obj, filters)
	}

	final private[Services] def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getAllObjectsOfClassImplementation(obj, fields)
	}

	final private[Services] def commitObjectToDatabase(i: StorableClass): Unit = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (i.valuesList.isEmpty) throw new Exception("Refusing to commit object with empty valuesList: " + i.getCompanion.entityName)
		else commitObjectToDatabaseImplementation(i)
	}

	final private[Services] def executeQueryBuilder(qb: QueryBuilder): List[QueryBuilderResultRow] = {
		executeQueryBuilderImplementation(qb)
	}

	final def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		if (TestUserType(pq.allowedUserTypes, rc.auth.companion)) executePreparedQueryForSelectImplementation(pq, fetchSize)
		else throw new UnauthorizedAccessException("executePreparedQueryforSelect denied to userType " + rc.auth.getClass.getName)
	}

	final def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else if (TestUserType(pq.allowedUserTypes, rc.auth.companion)) executePreparedQueryForInsertImplementation(pq)
		else throw new UnauthorizedAccessException("executePreparedQueryForInsert denied to userType " + rc.auth.name)
	}

	final def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else if (TestUserType(pq.allowedUserTypes, rc.auth.companion)) executePreparedQueryForUpdateOrDeleteImplementation(pq)
		else throw new UnauthorizedAccessException("executePreparedQueryForInsert denied to userType " + rc.auth.name)
	}

	final def executeProcedure[T](pc: PreparedProcedureCall[T]): T = {
		if (TestUserType(pc.allowedUserTypes, rc.auth.companion)) executeProcedureImpl(pc)
		else throw new UnauthorizedAccessException("executePreparedQueryforSelect denied to userType " + rc.auth.name)
	}

	// Implementations of PersistenceBroker should implement these.  Assume user type security has already been passed if you're calling these
	protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T]

	protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]

	protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T]

	protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T]

	protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit

	protected def executeQueryBuilderImplementation(qb: QueryBuilder): List[QueryBuilderResultRow]

	protected def executePreparedQueryForSelectImplementation[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T]

	protected def executePreparedQueryForInsertImplementation(pq: HardcodedQueryForInsert): Option[String]

	protected def executePreparedQueryForUpdateOrDeleteImplementation(pq: HardcodedQueryForUpdateOrDelete): Int

	protected def executeProcedureImpl[T](pc: PreparedProcedureCall[T]): T

	def testDB
}
