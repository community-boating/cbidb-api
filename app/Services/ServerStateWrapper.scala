package Services

import javax.inject.Inject

import CbiUtil.Initializable
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

// No idea if this is the right way to do what I'm trying to do.
// I'm hoping to avoid having to learn how the fuck Guice actually works for as long as possible
class ServerStateWrapper @Inject() (lifecycle: ApplicationLifecycle, poolConstructor: OracleConnectionPoolConstructor) {
  // Initialize server state
  ServerStateWrapper.serverState.peek match {
    case None => {
      println(" ***************     SETTING SERVER STATE   ***************  ")
      println("Using runmode: ROOT_MODE")
      RelationalBroker.initialize(poolConstructor, () => {
        lifecycle.addStopHook(() => Future.successful({
          println("****************************    Stop hook: closing pools  **********************")
          RelationalBroker.shutdown()
        }))
      })
      val pb: PersistenceBroker = new OracleBroker()
      val cb: CacheBroker = new RedisBroker()
      ServerStateWrapper.serverState.set(ServerState(
        new PermissionsAuthority(ServerRunMode.STAFF_MODE),
        0
      ))
    }
    case Some(_) => // This is not the first instance of SSW; do nothing on construction
  }

  def ss: ServerState = ServerStateWrapper.serverState.get
}

object ServerStateWrapper {
  protected val serverState = new Initializable[ServerState]

  def ss: ServerState = serverState.get
}