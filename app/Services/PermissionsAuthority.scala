package Services

import CbiUtil.Initializable
import Entities.Person
import play.api.mvc.{AnyContent, Request}

class PermissionsAuthority (val rm: ServerRunMode) {
  println("############## SETTING RUN MODE ############")
  PermissionsAuthority.rm.set(rm)
}

object PermissionsAuthority {
  val rm = new Initializable[ServerRunMode]
  private val myPB = new OracleBroker
  def spawnRequestCache(request: Request[_]): RequestCache = {
    val pb = new OracleBroker
    val cb = new RedisBroker
    new RequestCache(request, pb, cb)
  }

  def requestIsFromLocalHost(request: Request[AnyContent]): Boolean = {
    val allowedIPs = Set(
      "127.0.0.1",
      "0:0:0:0:0:0:0:1"
    )
    allowedIPs.contains(request.remoteAddress)
  }

  def getPwHashForPerson(request: Request[AnyContent], email: String): Option[(Int, String)] = {
    if (!requestIsFromLocalHost(request)) {
      None
    } else {
      val personsUnfiltered = myPB.getObjectsByFilters(
        Person,
        List(Person.fields.email.equalsConstantLowercase(Some(email)))
      )

      val personsFiltered = personsUnfiltered.filter(p => p.values.pwHash.get.isDefined)

      if (personsFiltered.length == 1) Some(1, personsFiltered.head.values.pwHash.get.get)
      else None
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
}