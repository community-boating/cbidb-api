package Services

import javax.inject.Inject

import CbiUtil.Initializable
import Services.PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE
import play.api.inject.ApplicationLifecycle
import play.api.{Application, Mode}

import scala.concurrent.Future


class ServerBootLoader @Inject()(
  lifecycle: ApplicationLifecycle,
  poolConstructor: OracleConnectionPoolConstructor,
  application: Application
) {
  // Boot up the server
  ServerBootLoader.serverStateContainer.peek match {
    case None => {
      println(" ***************     BOOTING UP SERVER   ***************  ")

      // Get server instance properties
      val serverProps = new ServerInstanceProperties("conf/private/server-properties")

      // Initialize database connections; provide shutdown callback
      RelationalBroker.initialize(poolConstructor, () => {
        lifecycle.addStopHook(() => Future.successful({
          println("****************************    Stop hook: closing pools  **********************")
          RelationalBroker.shutdown()
        }))
      })

      // Play runmode, i.e. Prod, Dev, or Test
      val playMode: Mode = application.mode
      PermissionsAuthority.playMode.set(playMode)
      println("Running in mode: " + playMode)

      // Initialize server state container
      ServerBootLoader.serverStateContainer.set(ServerStateContainer(
        serverTimeOffsetSeconds = 0
      ))

      // Initialize PermissionsAuthority with activated AuthenticationMechanisms
      PermissionsAuthority.allowableUserTypes.set(serverProps.enabledAuthMechanisms)

      // Init PA with persistence system
      PermissionsAuthority.persistenceSystem.set(PERSISTENCE_SYSTEM_ORACLE)
    }
    case Some(_) => // The server is already booted up; do nothing
  }
}

object ServerBootLoader {
  protected val serverStateContainer = new Initializable[ServerStateContainer]
  def ssc: ServerStateContainer = serverStateContainer.get
}