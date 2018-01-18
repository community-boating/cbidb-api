package Services

import CbiUtil.Initializable
import Services.Authentication.UserType
import play.api.Mode
import play.api.mvc.{AnyContent, Request}

object PermissionsAuthority {
  val allowableUserTypes = new Initializable[Set[UserType]]
  val persistenceSystem = new Initializable[PersistenceSystem]
  val playMode = new Initializable[Mode]
  val apexToken = new Initializable[String]
  def getPersistenceSystem: PersistenceSystem = persistenceSystem.get

  val SEC_COOKIE_NAME = "CBIDB-SEC"
  private val rootPB = RequestCache.getRootRC.pb
  private val rootCB = new RedisBroker

  def requestIsFromLocalHost(request: Request[AnyContent]): Boolean = {
    val allowedIPs = Set(
      "127.0.0.1",
      "0:0:0:0:0:0:0:1"
    )
    allowedIPs.contains(request.remoteAddress)
  }

  def getRequestCache(request: Request[AnyContent]): RequestCache = RequestCache.construct(request, rootCB)

  def getPwHashForUser(request: Request[AnyContent], userName: String, userType: UserType): Option[(Int, String)] = {
    if (
      allowableUserTypes.get.contains(userType) &&  // requested user type is enabled in this server instance
      requestIsFromLocalHost(request)               // request came from localhost, i.e. the bouncer
    ) userType.getPwHashForUser(userName, rootPB)
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