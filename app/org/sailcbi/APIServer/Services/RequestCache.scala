package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.{MembershipType, MembershipTypeExp, ProgramType, Rating}
import org.sailcbi.APIServer.IO.HTTP.FromWSClient
import org.sailcbi.APIServer.IO.PreparedQueries.{HardcodedQueryForInsert, HardcodedQueryForSelect, HardcodedQueryForUpdateOrDelete, PreparedProcedureCall}
import org.sailcbi.APIServer.IO.StripeIOController
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Services.Authentication._
import org.sailcbi.APIServer.Services.Exception.CORSException
import org.sailcbi.APIServer.Services.StripeAPIIO.{StripeAPIIOLiveService, StripeAPIIOMechanism}
import org.sailcbi.APIServer.Services.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.Storable.Fields.DatabaseField
import org.sailcbi.APIServer.Storable.StorableQuery.{QueryBuilder, QueryBuilderResultRow}
import org.sailcbi.APIServer.Storable.{Filter, StorableClass, StorableObject}
import play.api.libs.ws.WSClient

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext

// TODO: Some sort of security on the CacheBroker so arbitrary requests can't see the authentication tokens
// TODO: mirror all PB methods on RC so the RC can either pull from redis or dispatch to oracle etc
abstract class RequestCache private[Services](
	val userName: String,
	secrets: PermissionsAuthoritySecrets
)(implicit val PA: PermissionsAuthority) {
	private val self = this

	def companion: RequestCacheObject[_]

	private val pb: PersistenceBroker = {
		println("In RC:  " + PA.toString)
		val pbReadOnly = PA.readOnlyDatabase
		if (this.isInstanceOf[RootRequestCache]) new OracleBroker(secrets.dbConnection, false, pbReadOnly)
		else new OracleBroker(secrets.dbConnection, PA.preparedQueriesOnly, pbReadOnly)
	}

	val cb: CacheBroker = new RedisBroker

	final def getObjectById[T <: StorableClass](obj: StorableObject[T], id: Int): Option[T] =
		pb.getObjectById(obj, id)

	final def getObjectsByIds[T <: StorableClass](obj: StorableObject[T], ids: List[Int], fetchSize: Int = 50): List[T] =
		pb.getObjectsByIds(obj, ids, fetchSize)

	final def countObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter]): Int = {
		pb.countObjectsByFilters(obj, filters)
	}

	final def getObjectsByFilters[T <: StorableClass](obj: StorableObject[T], filters: List[String => Filter], fetchSize: Int = 50): List[T] =
		pb.getObjectsByFilters(obj, filters, fetchSize)

	final def getAllObjectsOfClass[T <: StorableClass](obj: StorableObject[T], fields: Option[List[DatabaseField[_]]] = None): List[T] =
		pb.getAllObjectsOfClass(obj, fields)

	final def commitObjectToDatabase(i: StorableClass): Unit =
		pb.commitObjectToDatabase(i)

	final def executePreparedQueryForSelect[T](pq: HardcodedQueryForSelect[T], fetchSize: Int = 50): List[T] = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForSelect(pq, fetchSize)
	}

	final def executePreparedQueryForInsert(pq: HardcodedQueryForInsert): Option[String] = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForInsert(pq)
	}

	final def executePreparedQueryForUpdateOrDelete(pq: HardcodedQueryForUpdateOrDelete): Int = {
		this.companion.test(pq.allowedUserTypes)
		pb.executePreparedQueryForUpdateOrDelete(pq)
	}

	final def executeQueryBuilder(qb: QueryBuilder): List[QueryBuilderResultRow] = {
		pb.executeQueryBuilder(qb)
	}

	final def executeProcedure[T](pc: PreparedProcedureCall[T]): T = {
		this.companion.test(pc.allowedUserTypes)
		pb.executeProcedure(pc)
	}

	final def testDB(): Unit = pb.testDB

	private def getStripeAPIIOMechanism(ws: WSClient)(implicit exec: ExecutionContext): StripeAPIIOMechanism = new StripeAPIIOLiveService(
		PermissionsAuthority.stripeURL,
		secrets.stripeSecretKey,
		new FromWSClient(ws)
	)

	private lazy val stripeDatabaseIOMechanism = new StripeDatabaseIOMechanism(pb)

	def getStripeIOController(ws: WSClient)(implicit exec: ExecutionContext): StripeIOController[T_User] = new StripeIOController(
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