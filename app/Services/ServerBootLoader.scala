package Services

import CbiUtil.Initializable
import IO.HTTP.FromWSClient
import IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import Services.PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE
import javax.inject.Inject
import play.api.inject.ApplicationLifecycle
import play.api.{Application, Mode}

import scala.concurrent.ExecutionContext.Implicits.global
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

      val debugSignet: Option[String] = getOptionalProperty(serverProps, "ApexDebugSignet")

      PermissionsAuthority.setApexDebugSignet(debugSignet)

      println("set apex debug signet to " + debugSignet)

      val symonSalt: Option[String] = getOptionalProperty(serverProps, "SymonSalt")
      PermissionsAuthority.setSymonSalt(symonSalt)

      PermissionsAuthority.instanceName.set(poolConstructor.getMainSchemaName)
      println("Using CBI DB instance: " + poolConstructor.getMainSchemaName)

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
      println("Prepared queries only: " + PermissionsAuthority.preparedQueriesOnly.get)

      // Init PA with persistence system
      PermissionsAuthority.persistenceSystem.set(PERSISTENCE_SYSTEM_ORACLE)

      PermissionsAuthority.stripeAPIIOMechanism.set(
        ws => new StripeAPIIOLiveService(PermissionsAuthority.stripeURL, serverProps.getProperty("StripeAPIKey"), new FromWSClient(ws))
      )

      try {
        PermissionsAuthority.testDB
      } catch {
        case e: Throwable =>
      }
    }
    case Some(_) => // The server is already booted up; do nothing
  }

  def getOptionalProperty(serverProps: ServerInstanceProperties, propname: String): Option[String] = {
    try {
      serverProps.getProperty(propname) match {
        case null => None
        case "" => None
        case s: String => Some(s)
      }
    } catch {
      case _: Throwable => None
    }
  }
}

object ServerBootLoader {
  protected val serverStateContainer = new Initializable[ServerStateContainer]
  def ssc: ServerStateContainer = serverStateContainer.get
}