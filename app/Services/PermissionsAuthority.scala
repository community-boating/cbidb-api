package Services

import java.math.BigInteger
import java.security.MessageDigest
import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}

import CbiUtil.{Initializable, ParsedRequest}
import IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import Services.Authentication._
import Services.Emailer.SSMTPEmailer
import Services.Logger.{Logger, ProductionLogger, UnitTestLogger}
import play.api.Mode
import play.api.libs.ws.WSClient
import play.api.mvc.{AnyContent, Request}

object PermissionsAuthority {
  val stripeURL: String = "https://api.stripe.com/v1/"
  val SEC_COOKIE_NAME = "CBIDB-SEC"
  val ROOT_AUTH_HEADER = "origin-root"
  val BOUNCER_AUTH_HEADER = "origin-bouncer"

  val allowableUserTypes = new Initializable[Set[UserType]]
  val persistenceSystem = new Initializable[PersistenceSystem]
  def getPersistenceSystem: PersistenceSystem = persistenceSystem.get
  val playMode = new Initializable[Mode]
  val preparedQueriesOnly = new Initializable[Boolean]
  val instanceName = new Initializable[String]

  private val apexDebugSignet = new Initializable[Option[String]]
  def setApexDebugSignet(os: Option[String]): Option[String] = apexDebugSignet.set(os)
  private val apexToken = new Initializable[String]
  def setApexToken(s: String): String = apexToken.set(s)
  private val stripeAPIKey = new Initializable[String]
  def setStripeAPIKey(s: String): String = stripeAPIKey.set(s)
  private val symonSalt = new Initializable[Option[String]]
  def setSymonSalt(s: Option[String]): Option[String] = symonSalt.set(s)

  val stripeAPIIOMechanism: Secret[WSClient => StripeAPIIOMechanism] = new Secret(rc => rc.auth.userType == ApexUserType)
  val stripeDatabaseIOMechanism: Secret[PersistenceBroker => StripeDatabaseIOMechanism] = new Secret(rc => rc.auth.userType == ApexUserType)
    .setImmediate(pb => new StripeDatabaseIOMechanism(pb))

  private lazy val rootPB = RequestCache.getRootRC.pb
  private lazy val rootCB = new RedisBroker
  private lazy val bouncerPB = RequestCache.getBouncerRC.pb

  def testDB = rootPB.testDB

  // TODO: is this right?
  lazy val isProd: Boolean = {
    try {
      playMode.peek.get
      true
    } catch {
      case _: Throwable => false
    }
  }

  // This should only be called by the unit tester.
  // If it's ever called when the application is runnning, it shoudl return None.
  // If the unit tester is running then the initializables aren't set,
  // so try to get their value, catch the exception, and return the PB
  def getRootPB: Option[PersistenceBroker] = {
    if (isProd) None else {
      println("@@@@ Giving away the root PB; was this the test runner?")
      Some(rootPB)
    }
  }

  def logger: Logger = if (isProd) new ProductionLogger(new SSMTPEmailer(Some("jon@community-boating.org"))) else new UnitTestLogger

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
    RequestCache.construct(requiredUserType, requiredUserName, parsedRequest, rootCB, apexToken.get)

  def getRequestCacheMember(
    requiredUserName: Option[String],
    parsedRequest: ParsedRequest,
    juniorId: Option[Int]
  ): (AuthenticationInstance, Option[RequestCache]) = {
    // TODO: bail if junior is is set and is not a valid junior for this member
    RequestCache.construct(MemberUserType, requiredUserName, parsedRequest, rootCB, apexToken.get)
  }


  def getPwHashForUser(request: ParsedRequest, userName: String, userType: UserType): Option[(Int, String)] = {
    if (
      allowableUserTypes.get.contains(userType) &&  // requested user type is enabled in this server instance
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
    val input = symonSalt.get.get + List(host, program, argString, status.toString, mac, now).mkString("-") + symonSalt.get.get
    println(input)
    val md5Bytes = MessageDigest.getInstance("MD5").digest(input.getBytes)
    val expectedHash = String.format("%032X", new BigInteger(1, md5Bytes))
    println("expectedHash: " + expectedHash)
    println("candidateHash: " + candidateHash)
    expectedHash == candidateHash
  }

  def validateApexSignet(candidate: Option[String]): Boolean = apexDebugSignet.getOrElse(None) == candidate

  def authenticate(parsedRequest: ParsedRequest): AuthenticationInstance = {
    val ret: Option[AuthenticationInstance] = PermissionsAuthority.allowableUserTypes.get
      .filter(_ != PublicUserType)
      .foldLeft(None: Option[AuthenticationInstance])((retInner: Option[AuthenticationInstance], ut: UserType) => retInner match {
        // If we already found a valid auth mech, pass it through.  Else hand the auth mech our cookies/headers etc and ask if it matches
        case Some(x) => Some(x)
        case None => ut.getAuthenticatedUsernameInRequest(parsedRequest, rootCB, apexToken.get) match {
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