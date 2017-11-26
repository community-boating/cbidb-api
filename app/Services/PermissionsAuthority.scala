package Services

import CbiUtil.Initializable
import play.api.mvc.Request

class PermissionsAuthority (val rm: ServerRunMode) {
  println("############## SETTING RUN MODE ############")
  PermissionsAuthority.rm.set(rm)
}

object PermissionsAuthority {
  val rm = new Initializable[ServerRunMode]
  def spawnRequestCache(request: Request[_]): RequestCache = {
    val pb = new OracleBroker
    val cb = new RedisBroker
    new RequestCache(request, pb, cb)
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