package com.coleji.framework.Core

import com.coleji.framework.Util.PropertiesWrapper
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

object OracleDatabaseConnection {
	def apply(confFileLocation: String): DatabaseHighLevelConnection = {
		val pw = new PropertiesWrapper(confFileLocation, Array[String]("username", "password", "host", "port", "sid", "schema", "temptableschema"))

		// TODO: unclear if this does anything
		Class.forName("oracle.jdbc.driver.OracleDriver")

		val mainSchemaName = pw.getProperty("schema")
		val tempSchemaName = pw.getProperty("temptableschema")
		val host = pw.getProperty("host")
		val port = pw.getProperty("port")
		val sid = pw.getProperty("sid")
		val username = pw.getProperty("username")
		val password = pw.getProperty("password")
		val tempUsername = pw.getProperty("temptableusername")
		val tempPassword = pw.getProperty("temptablepassword")

		val mainConfig = getDataSourceConfig(host, port, sid, username, password)
		val tempConfig = getDataSourceConfig(host, port, sid, tempUsername, tempPassword)

		DatabaseHighLevelConnection(
			mainPool = new ConnectionPoolWrapper(new HikariDataSource(mainConfig)),
			tempPool = new ConnectionPoolWrapper(new HikariDataSource(tempConfig)),
			mainSchemaName = mainSchemaName,
			tempSchemaName = tempSchemaName,
			mainUserName = username
		)
	}

	private def getDataSourceConfig(host: String, port: String, sid: String, username: String, password: String): HikariConfig = {
		val config = new HikariConfig()
		config.setJdbcUrl(s"jdbc:oracle:thin:@$host:$port:$sid")
		config.setUsername(username)
		config.setPassword(password)

		// other options
		config.setMaximumPoolSize(10)
		config.setLeakDetectionThreshold(30 * 1000)

		config
	}
}
