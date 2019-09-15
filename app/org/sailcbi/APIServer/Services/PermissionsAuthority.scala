package org.sailcbi.APIServer.Services

import java.math.BigInteger
import java.security.MessageDigest
import java.sql.ResultSet
import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import org.sailcbi.APIServer.CbiUtil.{Initializable, ParsedRequest}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.IO.PreparedQueries.HardcodedQueryForSelect
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import org.sailcbi.APIServer.IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.Services.Authentication._
import org.sailcbi.APIServer.Services.Emailer.SSMTPEmailer
import org.sailcbi.APIServer.Services.Logger.{Logger, ProductionLogger, UnitTestLogger}
import org.sailcbi.APIServer.Services.PermissionsAuthority.PersistenceSystem
import play.api.libs.ws.WSClient


class PermissionsAuthority private[Services] (
	val serverParameters: ServerParameters,
	val isTestMode: Boolean,
	val readOnlyDatabase: Boolean,
	private val dbConnection: DatabaseConnection,
	val allowableUserTypes: Set[UserType],
	private val apexToken: String,
	private val kioskToken: String,
	private val apexDebugSignet: Option[String],
	private val symonSalt: Option[String],
	val preparedQueriesOnly: Boolean,
	val persistenceSystem: PersistenceSystem,
	val stripeAPIIOMechanism: Secret[WSClient => StripeAPIIOMechanism],
	val stripeDatabaseIOMechanism: Secret[PersistenceBroker => StripeDatabaseIOMechanism]
)  {
	println(s"inside PermissionsAuthority constructor: test mode: $isTestMode, readOnlyDatabase: $readOnlyDatabase")
	println(this.toString)
	def instanceName: String = dbConnection.mainSchemaName

	private lazy val rootRC: RequestCache = new RequestCache(AuthenticationInstance.ROOT, dbConnection)
	private lazy val rootPB = rootRC.pb
	private lazy val rootCB = new RedisBroker

	private lazy val bouncerRC: RequestCache = new RequestCache(AuthenticationInstance.BOUNCER, dbConnection)
	private lazy val bouncerPB = bouncerRC.pb

	def testDB = rootPB.testDB

	def logger: Logger = if (!isTestMode) new ProductionLogger(new SSMTPEmailer(Some("jon@community-boating.org"))) else new UnitTestLogger

	def requestIsFromLocalHost(request: ParsedRequest): Boolean = {
		val allowedIPs = Set(
			"127.0.0.1",
			"0:0:0:0:0:0:0:1"
		)
		allowedIPs.contains(request.remoteAddress)
	}

	def getRequestCache(
		requiredUserType: NonMemberUserType,
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest
	): (AuthenticationInstance, Option[RequestCache]) =
		RequestCache.construct(
			requiredUserType,
			requiredUserName,
			parsedRequest,
			rootCB,
			apexToken,
			kioskToken,
			dbConnection
		)

	def getRequestCacheMember(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest
	): (AuthenticationInstance, Option[RequestCache]) =
		RequestCache.construct(MemberUserType, requiredUserName, parsedRequest, rootCB, apexToken, kioskToken, dbConnection)

	def getRequestCacheMemberWithJuniorId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		juniorId: Int
	): (AuthenticationInstance, Option[RequestCache]) = {
		println("about to validate junior id in request....")
		val ret = RequestCache.construct(MemberUserType, requiredUserName, parsedRequest, rootCB, apexToken, kioskToken, dbConnection)
		if (ret._1.userType == MemberUserType) {
			// auth was successful
			//... but does the request juniorId match the auth'd parent id?
			val authedPersonId = MemberUserType.getAuthedPersonId(ret._1.userName, rootPB)
			val getAuthedJuniorIDs = new HardcodedQueryForSelect[Int](Set(RootUserType)) {
				override def getQuery: String =
					s"""
					   |select b from person_relationships rl
					   |where a = ${authedPersonId}
					   |and rl.type_id = ${MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK}
					""".stripMargin
				override def mapResultSetRowToCaseObject(rs: ResultSet): Int = rs.getInt(1)
			}
			val juniorIds = rootPB.executePreparedQueryForSelect(getAuthedJuniorIDs)
			if (juniorIds.contains(juniorId)) {
				ret
			} else {
				throw new Exception(s"junior ID ${juniorId} in request does not match allowed ids for parent ${authedPersonId}: ${juniorIds.mkString(", ")}")
			}
		} else {
			// auth wasn't successful anyway
			ret
		}
	}

	def getRequestCacheMemberWithParentId(
		requiredUserName: Option[String],
		parsedRequest: ParsedRequest,
		parentId: Int
	): (AuthenticationInstance, Option[RequestCache]) = {
		val ret = RequestCache.construct(MemberUserType, requiredUserName, parsedRequest, rootCB, apexToken, kioskToken, dbConnection)
		if (ret._1.userType == MemberUserType) {
			// auth was successful
			//... but does the request parentId match the auth'd parent id?
			val authedPersonId = MemberUserType.getAuthedPersonId(ret._1.userName, rootPB)
			if (authedPersonId == parentId) {
				ret
			} else {
				throw new Exception(s"parent ID ${parentId} in request does not match authed parent ID ${authedPersonId}")
			}
		} else {
			// auth wasn't successful anyway
			ret
		}
	}

	def getPwHashForUser(request: ParsedRequest, userName: String, userType: UserType): Option[(Int, String)] = {
		if (
			allowableUserTypes.contains(userType) && // requested user type is enabled in this server instance
					authenticate(request).userType == BouncerUserType
		) userType.getPwHashForUser(userName, bouncerPB)
		else None
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
		val input = symonSalt.get + List(host, program, argString, status.toString, mac, now).mkString("-") + symonSalt.get
		println(input)
		val md5Bytes = MessageDigest.getInstance("MD5").digest(input.getBytes)
		val expectedHash = String.format("%032X", new BigInteger(1, md5Bytes))
		println("expectedHash: " + expectedHash)
		println("candidateHash: " + candidateHash)
		expectedHash == candidateHash
	}

	def validateApexSignet(candidate: Option[String]): Boolean = apexDebugSignet == candidate

	def authenticate(parsedRequest: ParsedRequest): AuthenticationInstance = {
		val ret: Option[AuthenticationInstance] = allowableUserTypes
				.filter(_ != PublicUserType)
				.foldLeft(None: Option[AuthenticationInstance])((retInner: Option[AuthenticationInstance], ut: UserType) => retInner match {
					// If we already found a valid auth mech, pass it through.  Else hand the auth mech our cookies/headers etc and ask if it matches
					case Some(x) => Some(x)
					case None => ut.getAuthenticatedUsernameInRequest(parsedRequest, rootCB, apexToken, kioskToken) match {
						case None => None
						case Some(x: String) => {
							println("AUTHENTICATION:  Request is authenticated as " + ut)
							Some(AuthenticationInstance(ut, x))
						}
					}
				})

		// If after looping through all auth mechs we still didnt find a match, this request is Public
		ret match {
			case Some(x) => x
			case None => {
				println("AUTHENTICATION:  No auth mechanisms matched; this is a Public request")
				AuthenticationInstance(PublicUserType, PublicUserType.uniqueUserName)
			}
		}
	}

	def assertRC(auth: AuthenticationInstance): RequestCache = {
		if (!isTestMode) throw new Exception("assertRC is for unit testing only")
		else new RequestCache(auth, dbConnection)
	}

	def closeDB(): Unit = dbConnection.close()
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

	class UnauthorizedAccessException(
		private val message: String = "Unauthorized Access Denied",
		private val cause: Throwable = None.orNull
	) extends Exception(message, cause)
}
