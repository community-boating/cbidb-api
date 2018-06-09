package Services

import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import CbiUtil.{Initializable, ParsedRequest}
import IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import Services.Authentication.{ApexUserType, AuthenticationInstance, UserType}
import Services.Emailer.SSMTPEmailer
import Services.Logger.{Logger, ProductionLogger, UnitTestLogger}
import play.api.Mode
import play.api.libs.ws.WSClient
import play.api.mvc.{AnyContent, Request}

object PermissionsAuthority {
  val stripeURL: String = "https://api.stripe.com/v1/"
  val SEC_COOKIE_NAME = "CBIDB-SEC"

  val allowableUserTypes = new Initializable[Set[UserType]]
  val persistenceSystem = new Initializable[PersistenceSystem]
  def getPersistenceSystem: PersistenceSystem = persistenceSystem.get
  val playMode = new Initializable[Mode]
  val preparedQueriesOnly = new Initializable[Boolean]
  val instanceName = new Initializable[String]

  val apexDebugSignet = new Initializable[Option[String]]
  def setApexDebugSignet(os: Option[String]): Option[String] = apexDebugSignet.set(os)

  private val apexToken = new Initializable[String]
  def setApexToken(s: String): String = apexToken.set(s)
  private val stripeAPIKey = new Initializable[String]
  def setStripeAPIKey(s: String): String = stripeAPIKey.set(s)
  private val symonSalt = new Initializable[String]
  def setSymonSalt(s: String): String = symonSalt.set(s)

  val stripeAPIIOMechanism: Secret[WSClient => StripeAPIIOMechanism] = new Secret(rc => rc.auth.userType == ApexUserType)
  val stripeDatabaseIOMechanism: Secret[PersistenceBroker => StripeDatabaseIOMechanism] = new Secret(rc => rc.auth.userType == ApexUserType)
    .setImmediate(pb => new StripeDatabaseIOMechanism(pb))

  private val rootPB = RequestCache.getRootRC.pb
  private val rootCB = new RedisBroker
  private val bouncerPB = RequestCache.getBouncerRC.pb

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

  def requestIsFromLocalHost(request: Request[AnyContent]): Boolean = {
    val allowedIPs = Set(
      "127.0.0.1",
      "0:0:0:0:0:0:0:1"
    )
    allowedIPs.contains(request.remoteAddress)
  }

  def requestIsVIP(request: Request[AnyContent]): Boolean = {
    request.headers.get("Is-VIP-Request") match {
      case Some("true") => true
      case _ => false
    }
  }

  def getRequestCache(
    requiredUserType: UserType,
    requiredUserName: Option[String],
    parsedRequest: ParsedRequest
  ): (AuthenticationInstance, Option[RequestCache]) =
    RequestCache.construct(requiredUserType, requiredUserName, parsedRequest, rootCB, apexToken.get)

  def getPwHashForUser(request: Request[AnyContent], userName: String, userType: UserType): Option[(Int, String)] = {
    if (
      allowableUserTypes.get.contains(userType) &&  // requested user type is enabled in this server instance
      requestIsFromLocalHost(request) &&               // request came from localhost, i.e. the bouncer
      requestIsVIP(request)
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
    val now: String = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH24"))
    val input = symonSalt.get + List(host, program, argString, status.toString, mac, now).mkString("-") + symonSalt.get
    val trueHash: String = MessageDigest.getInstance("MD5").digest(input.getBytes).mkString("")
    trueHash == candidateHash
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