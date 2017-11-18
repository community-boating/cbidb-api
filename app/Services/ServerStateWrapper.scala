package Services

import javax.inject.Inject

import Services.ServerStateWrapper.ServerState
import play.api.inject.ApplicationLifecycle

class ServerStateWrapper @Inject() (lifecycle: ApplicationLifecycle, poolConstructor: OracleConnectionPoolConstructor) {
  def get: ServerState = {
    if (ServerStateWrapper.isSet) ServerStateWrapper.get
    else {
      val pb: PersistenceBroker = new OracleBroker(lifecycle, poolConstructor)
      val cb: CacheBroker = new RedisBroker()
      ServerStateWrapper.init(new PermissionsAuthority(ServerRunMode.ROOT_MODE, pb, cb))
      ServerStateWrapper.get
    }
  }
}

object ServerStateWrapper {
  private var serverState: Option[ServerState] = None

  def get: ServerState = serverState match {
    case Some(s) => s
    case None => throw new Exception("Server state not yet initialized")
  }

  def isSet: Boolean = serverState.isDefined

  def init(pa: PermissionsAuthority): Unit = serverState match {
    case None =>
      serverState = Some(ServerState(pa))
    case Some(_) => println("Attempt to set new server state NOOP'd")
  }

  case class ServerState(pa: PermissionsAuthority)
}