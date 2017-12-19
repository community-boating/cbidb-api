package Services

import CbiUtil.Initializable
import Services.Authentication.{StaffUserType, UserType}
import play.api.Mode
import play.api.mvc.{AnyContent, Request}

object PermissionsAuthority {
  val allowableAuthenticationMechanisms = new Initializable[Set[UserType]]
  val persistenceSystem = new Initializable[PersistenceSystem]
  val playMode = new Initializable[Mode]
  def getPersistenceSystem: PersistenceSystem = persistenceSystem.get

  val SEC_COOKIE_NAME = "CBIDB-SEC"
  private val rootPB = new OracleBroker(StaffUserType)
  private val rootCB = new RedisBroker

  def requestIsFromLocalHost(request: Request[AnyContent]): Boolean = {
    val allowedIPs = Set(
      "127.0.0.1",
      "0:0:0:0:0:0:0:1"
    )
    allowedIPs.contains(request.remoteAddress)
  }


  def spawnRequestCache(userType: UserType, request: Request[AnyContent]): RequestCache =
    userType.spawnRequestCache(request, rootCB)

  def getPwHashForUser(userType: UserType, request: Request[AnyContent], userName: String): Option[(Int, String)] = {
    if (requestIsFromLocalHost(request)) {
      println(userType)
      userType.getPwHashForUser(request, userName, rootPB)
    } else None
  }

  def serverRunMode: Mode = play.api.Play.current.mode

  // TODO: replace with initializable set on server bootup (read from conf or something)


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