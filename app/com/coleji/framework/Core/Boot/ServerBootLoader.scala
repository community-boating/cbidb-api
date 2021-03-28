package com.coleji.framework.Core.Boot

import com.coleji.framework.Core.{DatabaseHighLevelConnection, OracleDatabaseConnection, PermissionsAuthority}
import org.sailcbi.APIServer.Server.{PermissionsAuthoritySecrets, ServerInstanceProperties, ServerParameters}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

abstract class ServerBootLoader {
	protected def load(lifecycle: Option[ApplicationLifecycle], isTestMode: Boolean, readOnlyDatabase: Boolean): PermissionsAuthority = {
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

		def getDBPools(attempts: Int = 0): DatabaseHighLevelConnection = {
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
					serverProps.getBoolean("PreparedQueriesOnly")
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
				isDebugMode = serverProps.getOptionalString("PADebug").getOrElse("false").equals("true"),
				isTestMode = isTestMode,
				readOnlyDatabase = readOnlyDatabase,
				allowableUserTypes = serverProps.enabledAuthMechanisms,
				preparedQueriesOnly = preparedQueriesOnly,
				persistenceSystem = PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE,
				secrets = PermissionsAuthoritySecrets(
					dbConnection = dbConnection,
					apexToken = serverProps.getString("ApexToken"),
					kioskToken = serverProps.getString("KioskToken"),
					apexDebugSignet = serverProps.getOptionalString("ApexDebugSignet"),
					symonSalt = serverProps.getOptionalString("SymonSalt"),
					stripeSecretKey = serverProps.getString("StripeAPIKey"),
					sentryDSN = serverProps.getOptionalString("sentryDSN")
				)
//				stripeAPIIOMechanism = (new Secret(rc => rc.auth.userType == ApexUserType))
//					.setImmediate(ws => new StripeAPIIOLiveService(PermissionsAuthority.stripeURL, , new FromWSClient(ws))),
//				stripeDatabaseIOMechanism = (new Secret(rc => rc.auth.userType == ApexUserType))
//					.setImmediate(pb => new StripeDatabaseIOMechanism(pb))
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
