package com.coleji.neptune.Core

import com.coleji.neptune.API.ResultError
import com.coleji.neptune.Core.Boot.SystemServerParameters
import com.coleji.neptune.Core.Emailer.SSMTPEmailer
import com.coleji.neptune.Core.Logger.{Logger, ProductionLogger, UnitTestLogger}
import com.coleji.neptune.Exception.{CORSException, PostBodyNotJSONException, UnauthorizedAccessException}
import com.coleji.neptune.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import com.coleji.neptune.Storable.{ResultSetWrapper, StorableClass, StorableObject}
import com.coleji.neptune.Util.{Initializable, PropertiesWrapper}
import com.redis.RedisClientPool
import io.sentry.Sentry
import play.api.libs.json.{JsResultException, JsValue}
import play.api.mvc.{Result, Results}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.io.Directory
import scala.reflect.runtime.universe
import scala.util.{Failure, Success}

class PermissionsAuthority private[Core] (
	val systemParams: SystemServerParameters,
	val customParams: PropertiesWrapper,
	dbGateway: DatabaseGateway,
	redisPool: RedisClientPool,
	paPostBoot: PropertiesWrapper => Unit,
)  {
	println(s"inside PermissionsAuthority constructor: test mode: ${systemParams.isTestMode}, readOnlyDatabase: ${systemParams.readOnlyDatabase}")
	println(systemParams)
	println(customParams)
	println("AllowableUserTypes: ", systemParams.allowableUserTypes)
	println("PA Debug: " + systemParams.isDebugMode)

	private lazy val rootRC: RootRequestCache = RootRequestCache.create(customParams, dbGateway, redisPool)
	// TODO: should this ever be used except by actual root-originated reqs e.g. crons?
	// e.g. there are some staff/member accessible functions that ultimately use this (even if they cant access rootPB directly)
	private lazy val rootCB = new RedisBroker(redisPool)

	lazy val logger: Logger = if (!systemParams.isTestMode) new ProductionLogger(new SSMTPEmailer(Some("jon@community-boating.org"))) else new UnitTestLogger

	paPostBoot(customParams)

	def instanceName: String = dbGateway.mainSchemaName

	def now(): LocalDateTime = {
		val q = new PreparedQueryForSelect[LocalDateTime](Set(RootRequestCache)) {
			override val params: List[String] = List()

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDateTime = rsw.getLocalDateTime(1)

			override def getQuery: String = "select util_pkg.get_sysdate from dual"
		}
		rootRC.executePreparedQueryForSelect(q).head
	}

	def currentSeason(): Int = {
		val q = new PreparedQueryForSelect[Int](Set(RootRequestCache)) {
			override val params: List[String] = List()

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): Int = rsw.getInt(1)

			override def getQuery: String = "select util_pkg.get_current_season from dual"
		}
		rootRC.executePreparedQueryForSelect(q).head
	}


	def requestIsFromLocalHost(request:ParsedRequest): Boolean = {
		val addressRegex = "127\\.0\\.0\\.1(:[0-9]+)?".r
		// TODO: this is stupid.  Replace with actual intelligent CIDR checking
		val dockerAddressRegex = "172\\.(?:16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)\\.[0-9]+\\.[0-9]+".r
		val allowedIPs = Set(
			"127.0.0.1",
			"0:0:0:0:0:0:0:1"
		)

		allowedIPs.contains(request.remoteAddress) ||
		addressRegex.findFirstIn(request.remoteAddress).isDefined ||
		dockerAddressRegex.findFirstIn(request.remoteAddress).isDefined
	}

	private def wrapInStandardTryCatch(block: () => Future[Result])(implicit exec: ExecutionContext): Future[Result] = {
		try {
			block().transform({
				case Success(r: Result) => Success(r)
				case Failure(e: Throwable) => {
					logger.error(e.getMessage, e)
					Sentry.capture(e)
					Success(Results.Status(400)(ResultError.UNKNOWN))
				}
			})
		} catch {
			case _: UnauthorizedAccessException => Future(Results.Status(400)(ResultError.UNAUTHORIZED))
			case _: CORSException => Future(Results.Status(400)(ResultError.UNAUTHORIZED))
			case e: PostBodyNotJSONException => {
				Sentry.capture(e)
				Future(Results.Status(400)(ResultError.NOT_JSON))
			}
			case e: JsResultException => {
				Sentry.capture(e)
				Future(Results.Status(400)(ResultError.PARSE_FAILURE(e.errors)))
			}
			case e: Throwable => {
				logger.error(e.getMessage, e)
				Sentry.capture(e)
				Future(Results.Status(400)(ResultError.UNKNOWN))
			}
		}
	}

	private def withRCWrapper[T <: RequestCache](
		get: () => Option[T],
		block: T => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] = {
		wrapInStandardTryCatch(() => get() match {
			case None => Future(Results.Ok(ResultError.UNAUTHORIZED))
			case Some(rc) => block(rc)
		})
	}

	private def getRequestCache[T <: RequestCache](
		requiredUserType: RequestCacheObject[T],
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest
	): Option[T] =
		authenticate(
			requiredUserType
		)(
			requiredUserName,
			parsedRequest,
			rootCB
		)

	def withRequestCacheNoFuture[T <: RequestCache, U](
		requiredUserType: RequestCacheObject[T]
	)(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		block: T => U
	): Option[U] = getRequestCache(requiredUserType, requiredUserName, parsedRequest).map(block)

	def withRequestCache[T <: RequestCache](
		requiredUserType: RequestCacheObject[T]
	)(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		block: T => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCache(requiredUserType, requiredUserName, parsedRequest), block)

	// TODO: better way to handle requests authenticated against multiple mechanisms?
	// TODO: any reason this should be in a companion obj vs just in teh PA?  Seems like only the PA should be making these things
	// TODO: synchronizing this is a temp solution until i get thread pools figured out.
	//  Doesn't really solve any problems anyway, except making the log a bit easier to read
	private def authenticate[T <: RequestCache](requiredUserType: RequestCacheObject[T], parsedRequest: ParsedRequest): Option[T] =
		authenticate(requiredUserType)(None, parsedRequest)

	@deprecated
	private def authenticate[T <: RequestCache](
		requiredUserType: RequestCacheObject[T]
	)(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		rootCB: CacheBroker = rootCB
	): Option[T] = synchronized {
		println("\n\n====================================================")
		println("====================================================")
		println("====================================================")
		println("Path: " + parsedRequest.path)
		println("Request received: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
		println("Headers: " + parsedRequest.headers)

		val authentication: Option[T] = requiredUserType.getAuthenticatedUsernameInRequest(
			parsedRequest,
			rootCB,
			customParams
		) match {
			case None => None
			case Some(x: String) => {
				println("AUTHENTICATION:  Request is authenticated as " + requiredUserType.getClass.getName)
				Some(requiredUserType.create(x, customParams, dbGateway, redisPool))
			}
		}

		// Cross-site request?
		// Someday I'll flesh this out more
		// For now, non-public requests may not be cross site
		// auth'd GETs are ok if the CORS status is Unknown
		val corsOK = {
			if (requiredUserType.requireCORSPass) {
				CORS.getCORSStatus(parsedRequest.headers) match {
					case Some(UNKNOWN) => parsedRequest.method == ParsedRequest.methods.GET
					case Some(CROSS_SITE) | None => false
					case _ => true
				}
			} else true
		}

		if (!corsOK) {
			println("@@@  Nuking RC due to potential CSRF")
			throw new CORSException
		} else {
			println("@@@  CSRF check passed")

			authentication
		}
	}

	def withParsedPostBodyJSON[T](body: Option[JsValue], ctor: JsValue => T)(block: T => Future[Result])(implicit exec: ExecutionContext): Future[Result] = {
		wrapInStandardTryCatch(() => {
			body match {
				case None => throw new PostBodyNotJSONException
				case Some(v: JsValue) => block(ctor(v))
				case Some(_) => throw new PostBodyNotJSONException
			}
		})
	}

	private def getAllEntityFiles(entityPackagePath: String): List[String] = {
		val folder = Directory("app/" + entityPackagePath.replace(".", "/"))
		folder.list.toList.map(path => "([^\\.]*)\\.scala".r.findFirstMatchIn(path.name).get.group(1))
	}

	private def companionForEntityFile[T](entityPackagePath: String, name: String)(implicit man: Manifest[T]): T = {
		val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
		val module = runtimeMirror.staticModule(entityPackagePath + "." + name + "$")
		runtimeMirror.reflectModule(module).instance.asInstanceOf[T]
	}

	private def checkAllValueListsMatchReflection(entityPackagePath: String): List[StorableObject[_]] = {
		val files = getAllEntityFiles(entityPackagePath)
		files.map(f => companionForEntityFile[StorableObject[_]](entityPackagePath, f)).filter(!_.valueListMatchesReflection)
	}

	private[Core] def bootChecks(): Unit = {
		if (systemParams.isDebugMode) {
			println("running PA boot checks")
			if (this.checkAllValueListsMatchReflection(systemParams.entityPackagePath).nonEmpty) {
				throw new Exception("ValuesList is not correct for: " + this.checkAllValueListsMatchReflection(systemParams.entityPackagePath))
			}
		} else {
			println("non debug mode, skipping boot checks")
		}
	}

	private[Core] def instantiateAllEntityCompanions(entityPackagePath: String): List[StorableObject[_]] = {
		val files = getAllEntityFiles(entityPackagePath)
		files.foreach(f => companionForEntityFile[Any](entityPackagePath, f))
		println(files)
		StorableObject.getEntities
	}

	private[Core] def checkAllEntitiesHaveValuesList(entityPackagePath: String): List[StorableObject[_]] = {
		val files = getAllEntityFiles(entityPackagePath)
		files.map(f => companionForEntityFile[StorableObject[_]](entityPackagePath, f)).filter(!_.hasValueList)
	}

	private[Core] def nukeDB(): Unit = {
		if (!systemParams.isTestMode) throw new Exception("nukeDB can only be called in test mode")
		else {
			this.instantiateAllEntityCompanions(systemParams.entityPackagePath)
			StorableObject.getEntities.foreach(e => {
				val q = new PreparedQueryForUpdateOrDelete(Set(RootRequestCache)) {
					override def getQuery: String = "delete from " + e.entityName
				}
				val result = rootRC.executePreparedQueryForUpdateOrDelete(q)
				println(s"deleted $result rows from ${e.entityName}")
			})
		}
	}

	private[Core] def withSeedState(entities: List[StorableClass], block: () => Unit): Unit = {
		if (!systemParams.isTestMode) throw new Exception("withSeedState can only be called in test mode")
		else {
			this.nukeDB()
			try {
				entities.foreach(e => rootRC.commitObjectToDatabase(e))
				block()
			} finally {
				this.nukeDB()
			}
		}
	}

	private[Core] def assertRC[T <: RequestCache](rco: RequestCacheObject[T], userName: String): T = {
		if (!systemParams.isTestMode) throw new Exception("assertRC is for unit testing only")
		else rco.create(userName, customParams, dbGateway, redisPool)
	}

	private[Core] def closeDB(): Unit = dbGateway.close()
}

object PermissionsAuthority {
	private var paWrapper: Initializable[PermissionsAuthority] = new Initializable[PermissionsAuthority]()

	def isBooted: Boolean = paWrapper.isInitialized
	implicit def PA: PermissionsAuthority = paWrapper.get
	implicit val persistenceSystem = PERSISTENCE_SYSTEM_ORACLE

	private[Core] def setPA(pa: PermissionsAuthority): PermissionsAuthority = paWrapper.set(pa)

	private[Core] def clearPA(): Unit = {
		println("in clearPA()")
		if (isBooted && paWrapper.get.systemParams.isTestMode) {
			this.paWrapper = new Initializable[PermissionsAuthority]()
		}
	}

	abstract class PersistenceSystem {
		val pbs: PersistenceBrokerStatic
	}

	trait PERSISTENCE_SYSTEM_RELATIONAL extends PersistenceSystem {
		override val pbs: RelationalBrokerStatic
	}

	case object PERSISTENCE_SYSTEM_ORACLE extends PERSISTENCE_SYSTEM_RELATIONAL {
		val pbs: RelationalBrokerStatic = OracleBrokerStatic
	}

	case object PERSISTENCE_SYSTEM_MYSQL extends PERSISTENCE_SYSTEM_RELATIONAL {
		val pbs: RelationalBrokerStatic = MysqlBrokerStatic
	}
}
