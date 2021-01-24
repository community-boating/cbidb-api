package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import org.sailcbi.APIServer.Storable._

// TODO: decide on one place for all the fetchSize defaults and delete the rest
abstract class PersistenceBroker private[Services](dbConnection: DatabaseHighLevelConnection, preparedQueriesOnly: Boolean, readOnly: Boolean) {
	// All public requests need to go through user type-based security
	final private[Services] def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectByIdImplementation(obj, id)
	}

	final private[Services] def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectsByIdsImplementation(obj, ids)
	}

	final private[Services] def countObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		countObjectsByFiltersImplementation(obj, filters)
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

	final private[Services] def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		executePreparedQueryForSelectImplementation(pq, fetchSize)
	}

	final private[Services] def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executePreparedQueryForInsertImplementation(pq)
	}

	final private[Services] def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executePreparedQueryForUpdateOrDeleteImplementation(pq)
	}

	final private[Services] def executeQueryBuilder(qb: QueryBuilder): List[QueryBuilderResultRow] = {
		// TODO: security
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else executeQueryBuilderImplementation(qb)
	}

	final private[Services] def executeProcedure[T](pc: PreparedProcedureCall[T]): T = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executeProcedureImpl(pc)
	}

	// Implementations of PersistenceBroker should implement these.  Assume user type security has already been passed if you're calling these
	protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T]

	protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]

	protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T]

	protected def countObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int

	protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T]

	protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit

	protected def executePreparedQueryForSelectImplementation[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T]

	protected def executePreparedQueryForInsertImplementation(pq: HardcodedQueryForInsert): Option[String]

	protected def executePreparedQueryForUpdateOrDeleteImplementation(pq: HardcodedQueryForUpdateOrDelete): Int

	protected def executeQueryBuilderImplementation(qb: QueryBuilder): List[QueryBuilderResultRow]

	protected def executeProcedureImpl[T](pc: PreparedProcedureCall[T]): T

	private[Services] def testDB
}
