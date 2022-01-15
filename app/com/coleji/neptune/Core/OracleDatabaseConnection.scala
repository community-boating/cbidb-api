package com.coleji.neptune.Core

import com.coleji.neptune.Util.PropertiesWrapper
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

object OracleDatabaseConnection {
	private[Core] def apply(confFileLocation: String): DatabaseGateway = {
		val pw = new PropertiesWrapper(confFileLocation, List("username", "password", "host", "port", "sid", "schema", "temptableschema"))

		// TODO: unclear if this does anything
		Class.forName("oracle.jdbc.driver.OracleDriver")

		val mainSchemaName = pw.getString("schema")
		val tempSchemaName = pw.getString("temptableschema")
		val host = pw.getString("host")
		val port = pw.getString("port")
		val sid = pw.getString("sid")
		val username = pw.getString("username")
		val password = pw.getString("password")
		val tempUsername = pw.getString("temptableusername")
		val tempPassword = pw.getString("temptablepassword")
		val poolSize = pw.getOptionalString("maxPoolSize").map(_.toInt)

		val mainConfig = getDataSourceConfig(host, port, sid, username, password, poolSize)
		val tempConfig = getDataSourceConfig(host, port, sid, tempUsername, tempPassword, poolSize)

		new DatabaseGateway(
			mainPool = new ConnectionPoolWrapper(new HikariDataSource(mainConfig)),
			tempPool = new ConnectionPoolWrapper(new HikariDataSource(tempConfig)),
			mainSchemaName = mainSchemaName,
			tempSchemaName = tempSchemaName,
			mainUserName = username
		)
	}

	private def getDataSourceConfig(host: String, port: String, sid: String, username: String, password: String, poolSize: Option[Int]): HikariConfig = {
		val config = new HikariConfig()
		val poolSizeToUse = poolSize.getOrElse(2)
		println("max pool size: " + poolSizeToUse)
		config.setJdbcUrl(s"jdbc:oracle:thin:@$host:$port:$sid")
		config.setUsername(username)
		config.setPassword(password)

		// other options
		config.setMaximumPoolSize(poolSizeToUse)
		config.setLeakDetectionThreshold(30 * 1000)

		config
	}
}
