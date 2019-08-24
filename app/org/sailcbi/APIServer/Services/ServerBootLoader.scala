package org.sailcbi.APIServer.Services

import org.sailcbi.APIServer.CbiUtil.Initializable
import org.sailcbi.APIServer.IO.HTTP.FromWSClient
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import org.sailcbi.APIServer.Services.PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE
import javax.inject.Inject
import play.api.inject.ApplicationLifecycle
import play.api.{Application, Mode}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServerBootLoader @Inject()(
	lifecycle: ApplicationLifecycle,
	oraclePoolConstructor: OracleConnectionPoolConstructor,
	mysqlPoolConstructor: MysqlConnectionPoolConstructor,
	application: Application
) {
	// Boot up the server

	def writeToTempFile(msg: String): Unit = {
		// For testing that DB connection pools are closed when the application is shut down

//		import java.io.BufferedWriter
//		import java.io.FileWriter
//
//		val writer = new BufferedWriter(new FileWriter("/home/jcole/tmp/out", true))
//		writer.newLine() //Add new line
//
//		writer.write(msg)
//		writer.close()
	}
	ServerBootLoader.serverStateContainer.peek match {
		case None => {
			println(" ***************     BOOTING UP SERVER   ***************  ")
			writeToTempFile("going up!")
			// Initialize server state container
			ServerBootLoader.serverStateContainer.set(ServerStateContainer(
				serverTimeOffsetSeconds = 0
			))

			// Get server instance properties
			val serverProps = new ServerInstanceProperties("conf/private/server-properties")

			// Initialize database connections; provide shutdown callback
			RelationalBroker.initialize(oraclePoolConstructor, () => {
				lifecycle.addStopHook(() => Future.successful({
					println("****************************    Stop hook: closing pools  **********************")
					writeToTempFile("coming down...")
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

			PermissionsAuthority.setKioskToken(serverProps.getProperty("KioskToken"))

			val debugSignet: Option[String] = getOptionalProperty(serverProps, "ApexDebugSignet")

			PermissionsAuthority.setApexDebugSignet(debugSignet)

			println("set apex debug signet to " + debugSignet)

			val symonSalt: Option[String] = getOptionalProperty(serverProps, "SymonSalt")
			PermissionsAuthority.setSymonSalt(symonSalt)

			PermissionsAuthority.instanceName.set(oraclePoolConstructor.getMainSchemaName)
			println("Using CBI DB instance: " + oraclePoolConstructor.getMainSchemaName)

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
	def setSSC(ssc: ServerStateContainer): Unit = serverStateContainer.set(ssc)
	def ssc: ServerStateContainer = serverStateContainer.get
}