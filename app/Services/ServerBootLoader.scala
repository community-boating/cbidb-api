package Services

import javax.inject.Inject

import CbiUtil.Initializable
import Services.Authentication.StaffUserType
import Services.PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future


class ServerBootLoader @Inject()(lifecycle: ApplicationLifecycle, poolConstructor: OracleConnectionPoolConstructor) {
  // Boot up the server
  ServerBootLoader.serverStateContainer.peek match {
    case None => {
      println(" ***************     BOOTING UP SERVER   ***************  ")

      // Get server instance properties
      val serverProps = new ServerInstanceProperties("conf/private/server-properties")
      println(serverProps.enabledAuthMechanisms)


      // Initialize database connections; provide shutdown callback
      RelationalBroker.initialize(poolConstructor, () => {
        lifecycle.addStopHook(() => Future.successful({
          println("****************************    Stop hook: closing pools  **********************")
          RelationalBroker.shutdown()
        }))
      })

      // Initialize server state container
      ServerBootLoader.serverStateContainer.set(ServerStateContainer(
        serverTimeOffsetSeconds = 0
      ))

      // Initialize PermissionsAuthority with activated AuthenticationMechanisms
      PermissionsAuthority.allowableAuthenticationMechanisms.set(Set(
        StaffUserType
      ))

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