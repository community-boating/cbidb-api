package com.coleji.framework.Core

import com.coleji.framework.Exception.UnauthorizedAccessException
import com.coleji.framework.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import com.coleji.framework.Storable.{Filter, StorableClass, StorableObject}

// TODO: decide on one place for all the fetchSize defaults and delete the rest
abstract class PersistenceBroker private[Core](dbConnection: DatabaseGateway, preparedQueriesOnly: Boolean, readOnly: Boolean) {
	// All public requests need to go through user type-based security
	final def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int, fieldShutter: Set[DatabaseField[_]]): Option[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectByIdImplementation(obj, id, fieldShutter)
	}

	final def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectsByIdsImplementation(obj, ids)
	}

	final def countObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		countObjectsByFiltersImplementation(obj, filters)
	}

	final def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fieldShutter: Set[DatabaseField[_]] = Set.empty, fetchSize: Int = 50): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getObjectsByFiltersImplementation(obj, filters, fieldShutter, fetchSize)
	}

	final def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] = {
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else getAllObjectsOfClassImplementation(obj, fields)
	}

	final def commitObjectToDatabase(i: StorableClass): Unit = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else if (i.valuesList.isEmpty) throw new Exception("Refusing to commit object with empty valuesList: " + i.companion.entityName)
		else commitObjectToDatabaseImplementation(i)
	}

	final def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		executePreparedQueryForSelectImplementation(pq, fetchSize)
	}

	final def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executePreparedQueryForInsertImplementation(pq)
	}

	final def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executePreparedQueryForUpdateOrDeleteImplementation(pq)
	}

	final def executeQueryBuilder(qb: QueryBuilder): List[QueryBuilderResultRow] = {
		// TODO: security
		if (preparedQueriesOnly) throw new UnauthorizedAccessException("Server is in Prepared Queries Only mode.")
		else executeQueryBuilderImplementation(qb)
	}

	final def executeProcedure[T](pc: PreparedProcedureCall[T]): T = {
		if (readOnly) throw new UnauthorizedAccessException("Server is in Database Read Only mode.")
		else executeProcedureImpl(pc)
	}

	// Implementations of PersistenceBroker should implement these.  Assume user type security has already been passed if you're calling these
	protected def getObjectByIdImplementation[T <: StorableClass](obj: StorableObject[T], id: Int, fieldShutter: Set[DatabaseField[_]]): Option[T]

	protected def getObjectsByIdsImplementation[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T]

	protected def getObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fieldShutter: Set[DatabaseField[_]], fetchSize: Int = 50): List[T]

	protected def countObjectsByFiltersImplementation[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int

	protected def getAllObjectsOfClassImplementation[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T]

	protected def commitObjectToDatabaseImplementation(i: StorableClass): Unit

	protected def executePreparedQueryForSelectImplementation[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T]

	protected def executePreparedQueryForInsertImplementation(pq: HardcodedQueryForInsert): Option[String]

	protected def executePreparedQueryForUpdateOrDeleteImplementation(pq: HardcodedQueryForUpdateOrDelete): Int

	protected def executeQueryBuilderImplementation(qb: QueryBuilder): List[QueryBuilderResultRow]

	protected def executeProcedureImpl[T](pc: PreparedProcedureCall[T]): T
}
