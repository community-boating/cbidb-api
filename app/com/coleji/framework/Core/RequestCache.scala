package com.coleji.framework.Core

import com.coleji.framework.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import com.coleji.framework.Storable.{Filter, StorableClass, StorableObject}
import com.coleji.framework.Util.PropertiesWrapper
import com.redis.RedisClientPool

// TODO: Some sort of security on the CacheBroker so arbitrary requests can't see the authentication tokens
// TODO: mirror all PB methods on RC so the RC can either pull from redis or dispatch to oracle etc
sealed abstract class RequestCache private[Core](
	val userName: String,
	serverParams: PropertiesWrapper,
	dbGateway: DatabaseGateway,
	redisPool: RedisClientPool
)(implicit val PA: PermissionsAuthority) {
	def companion: RequestCacheObject[_]

	protected val pb: PersistenceBroker = {
		if (this.isInstanceOf[RootRequestCache]) new OracleBroker(dbGateway, false, PA.systemParams.readOnlyDatabase)
		else new OracleBroker(dbGateway, PA.systemParams.preparedQueriesOnly, PA.systemParams.readOnlyDatabase)
	}

	val cb: CacheBroker = new RedisBroker(redisPool)

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
}

abstract class LockedRequestCache(
	override val userName: String,
	serverParams: PropertiesWrapper,
	dbGateway: DatabaseGateway,
	redisPool: RedisClientPool,
) extends RequestCache(userName, serverParams, dbGateway, redisPool) {

}

abstract class UnlockedRequestCache(
	override val userName: String,
	serverParams: PropertiesWrapper,
	dbGateway: DatabaseGateway,
	redisPool: RedisClientPool,
) extends RequestCache(userName, serverParams, dbGateway, redisPool) {
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