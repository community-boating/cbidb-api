package org.sailcbi.APIServer.Services.Boot

import org.sailcbi.APIServer.IO.HTTP.FromWSClient
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import org.sailcbi.APIServer.IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.Services.Authentication.ApexUserType
import org.sailcbi.APIServer.Services._
import play.api.inject.ApplicationLifecycle

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class ServerBootLoader {
	protected def load(lifecycle: Option[ApplicationLifecycle], isTestMode: Boolean): PermissionsAuthority = {
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

		def getDBPools(attempts: Int = 0): DatabaseConnection = {
			val MAX_ATTEMPTS = 3
			try {
				OracleDatabaseConnection("conf/private/oracle-credentials")
			} catch {
				case e: Exception => {
					if (attempts < MAX_ATTEMPTS) {
						println("failed to get pools, sleeping and trying again....")
						Thread.sleep(1000)
						getDBPools(attempts + 1)
					} else {
						throw e
					}
				}
			}
		}

		if (PermissionsAuthority.isBooted) PermissionsAuthority.PA
		else {
			println(" ***************     BOOTING UP SERVER   ***************  ")
			writeToTempFile("going up!")

			val dbConnection = getDBPools()
			lifecycle match {
				case Some(lc) => lc.addStopHook(() => Future.successful({
					println("****************************    Stop hook: closing pools  **********************")
					writeToTempFile("coming down...")
					dbConnection.close()
				}))
				case None =>
			}

			// Get server instance properties
			val serverProps = new ServerInstanceProperties("conf/private/server-properties")

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

			val PA = new PermissionsAuthority(
				serverParameters = ServerParameters(
					serverTimeOffsetSeconds = 0
				),
				isTestMode = isTestMode,
				dbConnection = dbConnection,
				allowableUserTypes = serverProps.enabledAuthMechanisms,
				apexToken = serverProps.getProperty("ApexToken"),
				kioskToken = serverProps.getProperty("KioskToken"),
				apexDebugSignet = getOptionalProperty(serverProps, "ApexDebugSignet"),
				symonSalt = getOptionalProperty(serverProps, "SymonSalt"),
				preparedQueriesOnly = preparedQueriesOnly,
				persistenceSystem = PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE,
				stripeAPIIOMechanism = (new Secret(rc => rc.auth.userType == ApexUserType))
					.setImmediate(ws => new StripeAPIIOLiveService(PermissionsAuthority.stripeURL, serverProps.getProperty("StripeAPIKey"), new FromWSClient(ws))),
				stripeDatabaseIOMechanism = (new Secret(rc => rc.auth.userType == ApexUserType))
					.setImmediate(pb => new StripeDatabaseIOMechanism(pb))
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
