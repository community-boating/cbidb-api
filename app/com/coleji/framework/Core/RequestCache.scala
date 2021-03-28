package com.coleji.framework.Core

import com.coleji.framework.IO.HTTP.FromWSClient
import com.coleji.framework.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import com.coleji.framework.Storable.Fields.DatabaseField
import com.coleji.framework.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import com.coleji.framework.Storable.{Filter, StorableClass, StorableObject}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{MembershipType, MembershipTypeExp, ProgramType, Rating}
import org.sailcbi.APIServer.IO.StripeAPIIO.{StripeAPIIOLiveService, StripeAPIIOMechanism}
import org.sailcbi.APIServer.IO.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.IO.StripeIOController
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Server.PermissionsAuthoritySecrets
import org.sailcbi.APIServer.UserTypes._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

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

	private def getStripeAPIIOMechanism(ws: WSClient)(implicit exec: ExecutionContext): StripeAPIIOMechanism = new StripeAPIIOLiveService(
		PermissionsAuthority.stripeURL,
		secrets.stripeSecretKey,
		new FromWSClient(ws)
	)

	private lazy val stripeDatabaseIOMechanism = new StripeDatabaseIOMechanism(pb)

	def getStripeIOController(ws: WSClient)(implicit exec: ExecutionContext): StripeIOController = new StripeIOController(
		this,
		getStripeAPIIOMechanism(ws),
		stripeDatabaseIOMechanism,
		PA.logger
	)

	// TODO: some way to confirm that things like this have no security on them (regardless of if we pass or fail in this req)
	// TODO: dont do this every request.
	object cachedEntities {
		lazy val programTypes: List[ProgramType] = pb.getAllObjectsOfClass(ProgramType)
		lazy val membershipTypes: List[MembershipType] = {
			pb.getAllObjectsOfClass(MembershipType).map(m => {
				m.references.program.findOneInCollection(programTypes)
				m
			})
		}
		lazy val membershipTypeExps: List[MembershipTypeExp] = {
			pb.getAllObjectsOfClass(MembershipTypeExp).map(me => {
				me.references.membershipType.findOneInCollection(membershipTypes)
				me
			})
		}
		lazy val ratings: List[Rating] = pb.getAllObjectsOfClass(Rating)
	}

	object logic {
		val dateLogic: DateLogic = new DateLogic(self)
	}
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