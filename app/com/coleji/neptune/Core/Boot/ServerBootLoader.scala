package com.coleji.neptune.Core.Boot

import com.coleji.neptune.Core.{DatabaseGateway, MysqlDatabaseConnection, OracleDatabaseConnection, PermissionsAuthority, RequestCacheObject}
import com.coleji.neptune.Util.PropertiesWrapper
import com.redis.RedisClientPool
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

object ServerBootLoader {
	val DB_DRIVER_ORACLE = "oracle"
	val DB_DRIVER_MYSQL = "mysql"

	private def getDBPools(dbDriver: String, attempts: Int = 0): DatabaseGateway = {
		val MAX_ATTEMPTS = 3
		try {
			dbDriver match {
				case DB_DRIVER_MYSQL => MysqlDatabaseConnection("conf/private/mysql-credentials")
				case _ => OracleDatabaseConnection("conf/private/oracle-credentials")
			}
		} catch {
			case e: Exception => {
				if (attempts < MAX_ATTEMPTS) {
					println("failed to get pools, sleeping and trying again....")
					Thread.sleep(1000)
					getDBPools(dbDriver, attempts + 1)
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


			val dbDriver = paramFile.getOptionalString("DBDriver") match {
				case Some(DB_DRIVER_MYSQL) => DB_DRIVER_MYSQL
				case _ => DB_DRIVER_ORACLE
			}

			println("Using DB Driver: " + dbDriver)

			val dbConnection = getDBPools(dbDriver)
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
					emailCrashesTo = paramFile.getOptionalString("EmailCrashesTo").getOrElse("jon@community-boating.org"),
					dbDriver = dbDriver
				),
				customParams = paramFile,
				dbGateway = dbConnection,
				redisPool = redisPool,
				paPostBoot
			)
		}
	}
}
