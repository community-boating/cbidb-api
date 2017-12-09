package Services

import CbiUtil.Initializable
import Entities.User
import Services.ServerRunMode.{ROOT_MODE, STAFF_MODE}
import play.api.mvc.{AnyContent, Request}

class PermissionsAuthority (val rm: ServerRunMode) {
  println("############## SETTING RUN MODE ############")
  PermissionsAuthority.rm.set(rm)
}

object PermissionsAuthority {
  val SEC_COOKIE_NAME = "CBIDB-SEC"
  val rm = new Initializable[ServerRunMode]
  private val myPB = new OracleBroker
  private val myCB = new RedisBroker
  def spawnRequestCache(request: Request[AnyContent]): RequestCache = {
    val authenticatedUserName = getUserForStaffRequest(request)
    println("AUTHENTICATED: " + authenticatedUserName)
    val proceed: Boolean = rm.get match {
      case ROOT_MODE => true
      case STAFF_MODE => authenticatedUserName.isDefined
      case _ => false
    }
    if (proceed) {
      val pb = new OracleBroker
      val cb = new RedisBroker
      val userName = if(authenticatedUserName.isDefined) authenticatedUserName.get else ""
      new RequestCache(userName, request, pb, cb)
    } else throw new UnauthorizedAccessException
  }

  def requestIsFromLocalHost(request: Request[AnyContent]): Boolean = {
    val allowedIPs = Set(
      "127.0.0.1",
      "0:0:0:0:0:0:0:1"
    )
    allowedIPs.contains(request.remoteAddress)
  }

  def getPwHashForUser(request: Request[AnyContent], userName: String): Option[(Int, String)] = {
    if (!requestIsFromLocalHost(request)) {
      None
    } else {
      val users = myPB.getObjectsByFilters(
        User,
        List(User.fields.userName.equalsConstantLowercase(userName))
      )

      if (users.length == 1) Some(1, users.head.values.pwHash.get)
      else None
    }
  }

  def getUserForStaffRequest(request: Request[AnyContent]): Option[String] = {
    println(request.cookies)
    val secCookies = request.cookies.filter(_.name == SEC_COOKIE_NAME)
    if (secCookies.isEmpty) None
    else if (secCookies.size > 1) None
    else {
      val cookie = secCookies.toList.head
      val token = cookie.value
      println(myCB.get("dfkjdgfjkdgfjkdgf"))
      val cacheResult = myCB.get(SEC_COOKIE_NAME + "_" + token)
      println(cacheResult)
      cacheResult match {
        case None => None
        case Some(s: String) => {
          val split = s.split(",")
          if (split.length != 2) None
          val userName = split(0)
          val expires = split(1)
          println("expires ")
          println(expires)
          println("and its currently ")
          println(System.currentTimeMillis())
          if (expires.toLong < System.currentTimeMillis()) None
          else Some(userName)
        }
      }
    }

  }

  // TODO: replace with initializable set on server bootup (read from conf or something)
  def getPersistenceSystem: PersistenceSystem = PERSISTENCE_SYSTEM_ORACLE

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