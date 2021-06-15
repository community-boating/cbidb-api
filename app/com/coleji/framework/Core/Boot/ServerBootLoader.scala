package com.coleji.framework.Core.Boot

import com.coleji.framework.Core.{DatabaseGateway, OracleDatabaseConnection, PermissionsAuthority, RequestCacheObject}
import com.coleji.framework.Util.PropertiesWrapper
import com.redis.RedisClientPool
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

object ServerBootLoader {
	private def getDBPools(attempts: Int = 0): DatabaseGateway = {
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

	private[Boot] def load(
		lifecycle: Option[ApplicationLifecycle],
		isTestMode: Boolean,
		readOnlyDatabase: Boolean,
		entityPackagePath: String,
		definedAuthMechanisms: List[(RequestCacheObject[_], String, PropertiesWrapper => Boolean)],
		requiredProperties: List[String],
		paPostBoot: PropertiesWrapper => Unit
	): PermissionsAuthority = {
		if (PermissionsAuthority.isBooted) PermissionsAuthority.PA
		else {
			println(" ***************     BOOTING UP SERVER   ***************  ")

			// Get server instance properties
			val paramFile = new PropertiesWrapper("conf/private/server-properties", requiredProperties)

			val enabledAuthMechanisms: List[RequestCacheObject[_]] =
				definedAuthMechanisms
					.filter(t => paramFile.getBoolean(t._2))
					.filter(t => t._3(paramFile)) // check the nuke function
					.map(t => t._1)

			val dbConnection = getDBPools()
			val redisPool = new RedisClientPool(paramFile.getOptionalString("RedisHost").getOrElse("localhost"), 6379)
			lifecycle match {
				case Some(lc) => lc.addStopHook(() => Future.successful({
					println("****************************    Stop hook: closing pools  **********************")
					dbConnection.close()
				}))
				case None =>
			}

			val preparedQueriesOnly = paramFile.getOptionalString("PreparedQueriesOnly").getOrElse("true").equals("true")

			new PermissionsAuthority(
				systemParams = SystemServerParameters(
					entityPackagePath = entityPackagePath,
					serverTimeOffsetSeconds = 0,
					isDebugMode = paramFile.getOptionalString("PADebug").getOrElse("false").equals("true"),
					isTestMode = isTestMode,
					readOnlyDatabase = readOnlyDatabase,
					allowableUserTypes = enabledAuthMechanisms,
					preparedQueriesOnly = preparedQueriesOnly,
					persistenceSystem = PermissionsAuthority.PERSISTENCE_SYSTEM_ORACLE,
				),
				customParams = paramFile,
				dbGateway = dbConnection,
				redisPool = redisPool,
				paPostBoot
			)
		}
	}
}
