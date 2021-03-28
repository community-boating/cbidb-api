package com.coleji.framework.Core

import com.coleji.framework.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import com.coleji.framework.Storable.{Filter, StorableClass, StorableObject}
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets

// TODO: Some sort of security on the CacheBroker so arbitrary requests can't see the authentication tokens
// TODO: mirror all PB methods on RC so the RC can either pull from redis or dispatch to oracle etc
sealed abstract class RequestCache private[Core](
	val userName: String,
	secrets: PermissionsAuthoritySecrets
)(implicit val PA: PermissionsAuthority) {
	private val self = this

	def companion: RequestCacheObject[_]

	protected val pb: PersistenceBroker = {
		println("In RC:  " + PA.toString)
		val pbReadOnly = PA.readOnlyDatabase
		if (this.isInstanceOf[RootRequestCache]) new OracleBroker(secrets.dbConnection, false, pbReadOnly)
		else new OracleBroker(secrets.dbConnection, PA.preparedQueriesOnly, pbReadOnly)
	}

	val cb: CacheBroker = new RedisBroker

	def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForSelect(pq, fetchSize)
	}

	def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForInsert(pq)
	}

	def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForUpdateOrDelete(pq)
	}

	def executeQueryBuilder(qb: QueryBuilder): List[QueryBuilderResultRow] = {
		pb.executeQueryBuilder(qb)
	}

	def executeProcedure[T](pc: PreparedProcedureCall[T]): T = {
		this.companion.test(pc.allowedUserTypes)
		pb.executeProcedure(pc)
	}

	def testDB(): Unit = pb.testDB

}

abstract class LockedRequestCache(
	override val userName: String,
	secrets: PermissionsAuthoritySecrets
) extends RequestCache(userName, secrets) {

}

abstract class UnlockedRequestCache(
	override val userName: String,
	secrets: PermissionsAuthoritySecrets
) extends RequestCache(userName, secrets) {
	def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] =
		pb.getObjectById(obj, id)

	def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] =
		pb.getObjectsByIds(obj, ids, fetchSize)

	def countObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int = {
		pb.countObjectsByFilters(obj, filters)
	}

	def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T] =
		pb.getObjectsByFilters(obj, filters, fetchSize)

	def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] =
		pb.getAllObjectsOfClass(obj, fields)

	def commitObjectToDatabase(i: StorableClass): Unit =
		pb.commitObjectToDatabase(i)
}