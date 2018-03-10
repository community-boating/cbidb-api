package Services

import CbiUtil.{Initializable, ParsedRequest}
import Services.Authentication.{AuthenticationInstance, UserType}
import Services.Secrets.SecretsObject
import play.api.Mode
import play.api.mvc.{AnyContent, Request}

object PermissionsAuthority {
  val allowableUserTypes = new Initializable[Set[UserType]]
  val persistenceSystem = new Initializable[PersistenceSystem]
  val playMode = new Initializable[Mode]
  val preparedQueriesOnly = new Initializable[Boolean]
  val instanceName = new Initializable[String]

  val stripeURL: String = "https://api.stripe.com/v1/"

  private val apexToken = new Initializable[String]
  def setApexToken(s: String) = apexToken.set(s)
  val secrets = new SecretsObject

  def getPersistenceSystem: PersistenceSystem = persistenceSystem.get

  val SEC_COOKIE_NAME = "CBIDB-SEC"
  private val rootPB = RequestCache.getRootRC.pb
  private val rootCB = new RedisBroker
  private val bouncerPB = RequestCache.getBouncerRC.pb

  // This should only be called by the unit tester.
  // If it's ever called when the application is runnning, it shoudl return None.
  // If the unit tester is running then the initializables aren't set,
  // so try to get their value, catch the exception, and return the PB
  def getRootPB: Option[PersistenceBroker] = {
    try {
      playMode.peek.get
      None
    } catch {
      case e: Throwable => {
        println("@@@@ Giving away the root PB; was this the test runner?")
        Some(rootPB)
      }
    }
  }

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