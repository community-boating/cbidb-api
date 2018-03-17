package Services

import javax.inject.Inject

import CbiUtil.Initializable
import IO.HTTP.FromWSClient
import IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import Services.PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE
import play.api.inject.ApplicationLifecycle
import play.api.{Application, Mode}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ServerBootLoader @Inject()(
  lifecycle: ApplicationLifecycle,
  poolConstructor: OracleConnectionPoolConstructor,
  application: Application
) {
  // Boot up the server
  ServerBootLoader.serverStateContainer.peek match {
    case None => {
      println(" ***************     BOOTING UP SERVER   ***************  ")
      // Initialize server state container
      ServerBootLoader.serverStateContainer.set(ServerStateContainer(
        serverTimeOffsetSeconds = 0
      ))

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

      // Initialize PermissionsAuthority with activated AuthenticationMechanisms
      PermissionsAuthority.allowableUserTypes.set(serverProps.enabledAuthMechanisms)
      println("Set enabled auth mechanisms: " + serverProps.enabledAuthMechanisms)

      // Init PA with APEX access token
      PermissionsAuthority.setApexToken(serverProps.getProperty("ApexToken"))


      PermissionsAuthority.instanceName.set(poolConstructor.getMainSchemaName)

      val preparedQueriesOnly = {
        try {
          println("raw:" + serverProps.getProperty("PreparedQueriesOnly"))
          println("case: " + serverProps.getProperty("PreparedQueriesOnly").toBoolean)
          serverProps.getProperty("PreparedQueriesOnly").toBoolean
        } catch {
          case t: Throwable => {
            println("error setting prepared queries only mode: " + t)
            true
          } // default to secure option
        }
      }
      PermissionsAuthority.preparedQueriesOnly.set(preparedQueriesOnly)

      // Init PA with persistence system
      PermissionsAuthority.persistenceSystem.set(PERSISTENCE_SYSTEM_ORACLE)

      PermissionsAuthority.stripeAPIIOMechanism.set(
        ws => new StripeAPIIOLiveService(PermissionsAuthority.stripeURL, serverProps.getProperty("StripeAPIKey"), new FromWSClient(ws))
      )
    }
    case Some(_) => // The server is already booted up; do nothing
  }
}

object ServerBootLoader {
  protected val serverStateContainer = new Initializable[ServerStateContainer]
  def ssc: ServerStateContainer = serverStateContainer.get
}