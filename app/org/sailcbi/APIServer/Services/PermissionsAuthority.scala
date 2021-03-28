package org.sailcbi.APIServer.Services

import com.coleji.framework.API.ResultError
import com.coleji.framework.IO.PreparedQueries.{HardcodedQueryForSelect, PreparedProcedureCall, PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import com.coleji.framework.Storable.{StorableClass, StorableObject}
import io.sentry.Sentry
import org.sailcbi.APIServer.CbiUtil.{Initializable, ParsedRequest}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Services.Authentication._
import org.sailcbi.APIServer.Services.Emailer.SSMTPEmailer
import org.sailcbi.APIServer.Services.Exception.{CORSException, PostBodyNotJSONException, UnauthorizedAccessException}
import org.sailcbi.APIServer.Services.Logger.{Logger, ProductionLogger, UnitTestLogger}
import org.sailcbi.APIServer.Services.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Storable.StorableObject
import play.api.libs.json.JsValue
import play.api.mvc.{Result, Results}

import java.math.BigInteger
import java.security.MessageDigest
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId, ZonedDateTime}
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.io.Directory
import scala.reflect.runtime.universe
import scala.util.{Failure, Success}


class PermissionsAuthority private[Services] (
	val serverParameters: ServerParameters,
	val isTestMode: Boolean,
	val isDebugMode: Boolean,
	val readOnlyDatabase: Boolean,
	val allowableUserTypes: List[RequestCacheObject[_]],
	val preparedQueriesOnly: Boolean,
	val persistenceSystem: PersistenceSystem,
	secrets: PermissionsAuthoritySecrets
)  {
	private val ENTITY_PACKAGE_PATH = "org.sailcbi.APIServer.Entities.EntityDefinitions"
	println(s"inside PermissionsAuthority constructor: test mode: $isTestMode, readOnlyDatabase: $readOnlyDatabase")
	println(this.toString)
	println("AllowableUserTypes: ", allowableUserTypes)
	println("PA Debug: " + this.isDebugMode)
	// Initialize sentry
	secrets.sentryDSN.foreach(Sentry.init)
	if (secrets.sentryDSN.isDefined) {
		println("sentry is defined")
	} else {
		println("sentry is NOT defined")
	}

	def bootChecks(): Unit = {
		if (this.isDebugMode) {
			println("running PA boot checks")
			if (this.checkAllValueListsMatchReflection.nonEmpty) {
				throw new Exception("ValuesList is not correct for: " + this.checkAllValueListsMatchReflection)
			}
		} else {
			println("non debug mode, skipping boot checks")
		}
	}

	def instanceName: String = secrets.dbConnection.mainSchemaName

	def sleep(): Unit = {
//		println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
//		println("sleepytime...")
//		println("Active threads: " + Thread.activeCount())
//		println("Current thread ID: " + Thread.currentThread().getId)
//		println("Current thread name: " + Thread.currentThread().getName)
//		println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
//		Thread.sleep(4000)
	}

	private lazy val rootRC: RootRequestCache = RootRequestCache.create(secrets)
	// TODO: should this ever be used except by actual root-originated reqs e.g. crons?
	// e.g. there are some staff/member accessible functions that ultimately use this (even if they cant access rootPB directly)
	private lazy val rootCB = new RedisBroker

	private lazy val bouncerRC: BouncerRequestCache = BouncerRequestCache.create(secrets)

	def now(): LocalDateTime = {
		val q = new PreparedQueryForSelect[LocalDateTime](Set(RootRequestCache)) {
			override val params: List[String] = List()

			override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): LocalDateTime = rsw.getLocalDateTime(1)

			override def getQuery: String = "select util_pkg.get_sysdate from dual"
		}
		rootRC.executePreparedQueryForSelect(q).head
	}

	def testDB = rootRC.testDB

	def procedureTest() = {
		println("starting executeProcedure...")
		val ret = rootRC.executeProcedure(PreparedProcedureCall.test)
		println("finished executeProcedure")

		println("procedure call complete with named params.....")
		println(s"(b, c, ss, persons) = $ret")
	}

	def logger: Logger = if (!isTestMode) new ProductionLogger(new SSMTPEmailer(Some("jon@community-boating.org"))) else new UnitTestLogger

	def requestIsFromLocalHost(request: ParsedRequest): Boolean = {
		val addressRegex = "127\\.0\\.0\\.1(:[0-9]+)?".r
		val allowedIPs = Set(
			"127.0.0.1",
			"0:0:0:0:0:0:0:1"
		)
		allowedIPs.contains(request.remoteAddress) || addressRegex.findFirstIn(request.remoteAddress).isDefined
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
			rootCB,
			secrets
		)

	def withRequestCache[T <: RequestCache](
		requiredUserType: RequestCacheObject[T]
	)(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		block: T => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCache(requiredUserType, requiredUserName, parsedRequest), block)

	private def getRequestCacheMember(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest
	): Option[MemberRequestCache] =
		authenticate(MemberRequestCache)(requiredUserName, parsedRequest, rootCB, secrets)

	def withRequestCacheMember(
		parsedRequest: ParsedRequest,
		block: MemberRequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCacheMember(None, parsedRequest), block)

	private def getRequestCacheMemberWithJuniorId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		juniorId: Int
	): Option[MemberRequestCache] = {
		println("about to validate junior id in request....")
		authenticate(MemberRequestCache)(requiredUserName, parsedRequest, rootCB, secrets) match {
			case None => None
			case Some(ret: MemberRequestCache) => {
				// auth was successful
				//... but does the request juniorId match the auth'd parent id?
				val authedPersonId = ret.asInstanceOf[MemberRequestCache].getAuthedPersonId()
				val getAuthedJuniorIDs = new HardcodedQueryForSelect[Int](Set(RootRequestCache)) {
					override def getQuery: String =
						s"""
						   |select b from person_relationships rl
						   |where a = ${authedPersonId}
						   |and rl.type_id = ${MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK}
				""".stripMargin
					override def mapResultSetRowToCaseObject(rs: ResultSetWrapper): Int = rs.getInt(1)
				}
				val juniorIds = rootRC.executePreparedQueryForSelect(getAuthedJuniorIDs)
				if (juniorIds.contains(juniorId)) {
					Some(ret)
				} else {
					throw new Exception(s"junior ID ${juniorId} in request does not match allowed ids for parent ${authedPersonId}: ${juniorIds.mkString(", ")}")
				}
			}
		}
	}

	def withRequestCacheMemberWithJuniorId(
		parsedRequest: ParsedRequest,
		juniorId: Int,
		block: MemberRequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCacheMemberWithJuniorId(None, parsedRequest, juniorId), block)

	private def getRequestCacheMemberWithParentId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		parentId: Int
	): Option[MemberRequestCache] = {
		authenticate(MemberRequestCache)(requiredUserName, parsedRequest, rootCB, secrets) match {
			case None => None
			case Some(ret: MemberRequestCache) => {
				// auth was successful
				//... but does the request parentId match the auth'd parent id?
				val authedPersonId = ret.getAuthedPersonId()
				if (authedPersonId == parentId) {
					Some(ret)
				} else {
					throw new Exception(s"parent ID ${parentId} in request does not match authed parent ID ${authedPersonId}")
				}
			}
		}
	}

	def withRequestCacheMemberWithParentId(
		parsedRequest: ParsedRequest,
		parentId: Int,
		block: MemberRequestCache => Future[Result]
	)(implicit exec: ExecutionContext): Future[Result] =
		withRCWrapper(() => getRequestCacheMemberWithParentId(None, parsedRequest, parentId), block)

	def getPwHashForUser(request: ParsedRequest, userName: String, userType: RequestCacheObject[_]): Option[(String, String, String)] = {
		if (
			allowableUserTypes.contains(userType) && // requested user type is enabled in this server instance
			BouncerRequestCache.getAuthenticatedUsernameInRequest(request, rootCB, secrets.apexToken, secrets.kioskToken).isDefined
		) {
			userType.getPwHashForUser(rootRC, userName)
		} else None
	}

	// TODO: better way to handle requests authenticated against multiple mechanisms?
	// TODO: any reason this should be in a companion obj vs just in teh PA?  Seems like only the PA should be making these things

	// TODO: synchronizing this is a temp solution until i get thread pools figured out.
	//  Doesn't really solve any problems anyway, except making the log a bit easier to read
	def authenticate[T <: RequestCache](
		requiredUserType: RequestCacheObject[T]
	)(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		rootCB: CacheBroker,
		secrets: PermissionsAuthoritySecrets
	): Option[T] = synchronized {
		println("\n\n====================================================")
		println("====================================================")
		println("====================================================")
		println("Path: " + parsedRequest.path)
		println("Request received: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
		println("Headers: " + parsedRequest.headers)

		val authentication: Option[T] = requiredUserType.getAuthenticatedUsernameInRequest(parsedRequest, rootCB, secrets.apexToken, secrets.kioskToken) match {
			case None => None
			case Some(x: String) => {
				println("AUTHENTICATION:  Request is authenticated as " + requiredUserType.getClass.getName)
				Some(requiredUserType.create(x)(secrets))
			}
		}

		// Cross-site request?
		// Someday I'll flesh this out more
		// For now, non-public requests may not be cross site
		// auth'd GETs are ok if the CORS status is Unknown
		val corsOK = {
			if (requiredUserType == StaffRequestCache || requiredUserType == MemberRequestCache) {
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

			// requiredUserType says what this request endpoint requires
			// If we authenticated as a superior auth (i.e. a staff member requested a public endpoint),
			// attempt to downgrade to the desired auth
			// For public endpoints this is overkill but I may one day implement staff making reqs on member endpoints,
			// so this architecture will make that request behave exactly as if the member requested it themselves
			//			if (authentication.isDefined) {
			//				Some()
			//			} else {
			//				requiredUserType.getAuthFromSuperiorAuth(authentication, requiredUserName) match {
			//					case Some(lowerAuth: AuthenticationInstance) => {
			//						println("@@@ Successfully downgraded to " + lowerAuth.userType)
			//						Some(new RequestCache(
			//							trueAuth = authentication,
			//							auth = lowerAuth,
			//							secrets
			//						))
			//					}
			//					case None => {
			//						println("@@@ Unable to downgrade auth to " + requiredUserType)
			//						None
			//					}
			//				}
			//			}
		}
	}

	def validateSymonHash(
		host: String,
		program: String,
		argString: String,
		status: Int,
		mac: String,
		candidateHash: String
	): Boolean = {
		println("here we go")
		val now: String = ZonedDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH").withZone(ZoneId.of("America/New_York")))
		val input = secrets.symonSalt.get + List(host, program, argString, status.toString, mac, now).mkString("-") + secrets.symonSalt.get
		println(input)
		val md5Bytes = MessageDigest.getInstance("MD5").digest(input.getBytes)
		val expectedHash = String.format("%032X", new BigInteger(1, md5Bytes))
		println("expectedHash: " + expectedHash)
		println("candidateHash: " + candidateHash)
		expectedHash == candidateHash
	}

	def validateApexSignet(candidate: Option[String]): Boolean = secrets.apexDebugSignet == candidate


//	def authenticate(parsedRequest: ParsedRequest): UserType = {
//		val ret: Option[UserType] = allowableUserTypes
//				.filter(_ != PublicUserType)
//				.foldLeft(None: Option[UserType])((retInner: Option[UserType], ut: RequestCacheObject[_ <: UserType]) => retInner match {
//					// If we already found a valid auth mech, pass it through.  Else hand the auth mech our cookies/headers etc and ask if it matches
//					case Some(x) => Some(x)
//					case None => ut.getAuthenticatedUsernameInRequest(parsedRequest, rootCB, secrets.apexToken, secrets.kioskToken) match {
//						case None => None
//						case Some(x: String) => {
//							println("AUTHENTICATION:  Request is authenticated as " + ut)
//							Some(ut.create(x))
//						}
//					}
//				})
//
//		// If after looping through all auth mechs we still didnt find a match, this request is Public
//		ret match {
//			case Some(x) => x
//			case None => {
//				println("AUTHENTICATION:  No auth mechanisms matched; this is a Public request")
//				AuthenticationInstance(PublicUserType, PublicUserType.uniqueUserName)
//			}
//		}
//	}

	def assertRC[T <: RequestCache](rco: RequestCacheObject[T], userName: String): T = {
		if (!isTestMode) throw new Exception("assertRC is for unit testing only")
		else rco.create(userName)(secrets)
	}

	def closeDB(): Unit = secrets.dbConnection.close()

	def withParsedPostBodyJSON[T](body: Option[JsValue], ctor: JsValue => T)(block: T => Future[Result])(implicit exec: ExecutionContext): Future[Result] = {
		wrapInStandardTryCatch(() => {
			body match {
				case None => throw new PostBodyNotJSONException
				case Some(v: JsValue) => block(ctor(v))
				case Some(_) => throw new PostBodyNotJSONException
			}
		})
	}



	def getAllEntityFiles: List[String] = {
		val folder = Directory("app/" + ENTITY_PACKAGE_PATH.replace(".", "/"))
		folder.list.toList.map(path => "([^\\.]*)\\.scala".r.findFirstMatchIn(path.name).get.group(1))
	}

	def getCompanionForEntityFile[T](name: String)(implicit man: Manifest[T]): T = {
		val runtimeMirror = universe.runtimeMirror(getClass.getClassLoader)
		val module = runtimeMirror.staticModule(ENTITY_PACKAGE_PATH + "." + name + "$")
		runtimeMirror.reflectModule(module).instance.asInstanceOf[T]
	}

	def instantiateAllEntityCompanions(): List[StorableObject[_]] = {
		val files = getAllEntityFiles
		files.foreach(f => getCompanionForEntityFile[Any](f))
		println(files)
		StorableObject.getEntities
	}

	def checkAllEntitiesHaveValuesList: List[StorableObject[_]] = {
		val files = getAllEntityFiles
		files.map(f => getCompanionForEntityFile[StorableObject[_]](f)).filter(!_.hasValueList)
	}

	def checkAllValueListsMatchReflection: List[StorableObject[_]] = {
		val files = getAllEntityFiles
		files.map(f => getCompanionForEntityFile[StorableObject[_]](f)).filter(!_.valueListMatchesReflection)
	}

	def nukeDB(): Unit = {
		if (!isTestMode) throw new Exception("nukeDB can only be called in test mode")
		else {
			this.instantiateAllEntityCompanions()
			StorableObject.getEntities.foreach(e => {
				val q = new PreparedQueryForUpdateOrDelete(Set(RootRequestCache)) {
					override def getQuery: String = "delete from " + e.entityName
				}
				val result = rootRC.executePreparedQueryForUpdateOrDelete(q)
				println(s"deleted $result rows from ${e.entityName}")
			})
		}
	}

	def withSeedState(entities: List[StorableClass], block: () => Unit): Unit = {
		if (!isTestMode) throw new Exception("withSeedState can only be called in test mode")
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
}


object PermissionsAuthority {
	private var paWrapper: Initializable[PermissionsAuthority] = new Initializable[PermissionsAuthority]()
	def setPA(pa: PermissionsAuthority): PermissionsAuthority = paWrapper.set(pa)
	def isBooted: Boolean = paWrapper.isInitialized
	implicit def PA: PermissionsAuthority = paWrapper.get
	def clearPA(): Unit = {
		println("in clearPA()")
		if (isBooted && paWrapper.get.isTestMode) {

			this.paWrapper = new Initializable[PermissionsAuthority]()
		}
	}

	val stripeURL: String = "https://api.stripe.com/v1/"
	val SEC_COOKIE_NAME = "CBIDB-SEC"
	val ROOT_AUTH_HEADER = "origin-root"
	val BOUNCER_AUTH_HEADER = "origin-bouncer"

	val EFUSE_REDIS_KEY_CBIDB_PUBLIC_WEB = "$$CBIDB_PUBLIC_WEB_EFUSE"

	trait PersistenceSystem {
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
