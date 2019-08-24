package org.sailcbi.APIServer.Services.Boot

import org.sailcbi.APIServer.IO.HTTP.FromWSClient
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import org.sailcbi.APIServer.Services.{OracleConnectionPoolConstructor, PermissionsAuthority, RelationalBroker, ServerInstanceProperties, ServerParameters}
import play.api.Mode
import play.api.inject.ApplicationLifecycle
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class ServerBootLoader {
	def load(
		oraclePoolConstructor: OracleConnectionPoolConstructor,
		lifecycle: Option[ApplicationLifecycle],
		playMode: play.api.Mode
	): PermissionsAuthority = {
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

		if (PermissionsAuthority.isBooted) PermissionsAuthority.PA
		else {
			val PA = new PermissionsAuthority(true)

			println(" ***************     BOOTING UP SERVER   ***************  ")
			writeToTempFile("going up!")
			// Initialize server params
			PA.setServerParameters(ServerParameters(
				serverTimeOffsetSeconds = 0
			))

			// Get server instance properties
			val serverProps = new ServerInstanceProperties("conf/private/server-properties")

			// Initialize database connections; provide shutdown callback
			RelationalBroker.initialize(oraclePoolConstructor, () => {
				lifecycle match {
					case Some(lc) => lc.addStopHook(() => Future.successful({
						println("****************************    Stop hook: closing pools  **********************")
						writeToTempFile("coming down...")
						RelationalBroker.shutdown()
					}))
					case None =>
				}
			})

			// Play runmode, i.e. Prod, Dev, or Test
			PA.playMode.set(playMode)
			println("Running in mode: " + playMode)

			// Initialize PermissionsAuthority with activated AuthenticationMechanisms
			PA.allowableUserTypes.set(serverProps.enabledAuthMechanisms)
			println("Set enabled auth mechanisms: " + serverProps.enabledAuthMechanisms)

			// Init PA with APEX access token
			PA.setApexToken(serverProps.getProperty("ApexToken"))

			PA.setKioskToken(serverProps.getProperty("KioskToken"))

			val debugSignet: Option[String] = getOptionalProperty(serverProps, "ApexDebugSignet")

			PA.setApexDebugSignet(debugSignet)

			println("set apex debug signet to " + debugSignet)

			val symonSalt: Option[String] = getOptionalProperty(serverProps, "SymonSalt")
			PA.setSymonSalt(symonSalt)

			PA.instanceName.set(oraclePoolConstructor.getMainSchemaName)
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
			PA.preparedQueriesOnly.set(preparedQueriesOnly)
			println("Prepared queries only: " + PA.preparedQueriesOnly.get)

			// Init PA with persistence system
			PA.persistenceSystem.set(PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE)

			PA.stripeAPIIOMechanism.set(
				ws => new StripeAPIIOLiveService(PermissionsAuthority.stripeURL, serverProps.getProperty("StripeAPIKey"), new FromWSClient(ws))
			)

			try {
				PA.testDB
			} catch {
				case e: Throwable =>
			}

			PA
		}
	}
}
