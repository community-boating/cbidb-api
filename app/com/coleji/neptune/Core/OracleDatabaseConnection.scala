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
		val poolSize = pw.getOptionalString("maxPoolSize").map(_.toInt).getOrElse(2)
		val poolSizeTemp = pw.getOptionalString("maxPoolSizeTemp").map(_.toInt).getOrElse(1)

		val mainConfig = getDataSourceConfig(host, port, sid, username, password, poolSize)
		val tempConfig = getDataSourceConfig(host, port, sid, tempUsername, tempPassword, poolSizeTemp)

		new DatabaseGateway(
			mainPool = new ConnectionPoolWrapper(new HikariDataSource(mainConfig)),
			tempPool = new ConnectionPoolWrapper(new HikariDataSource(tempConfig)),
			mainSchemaName = mainSchemaName,
			tempSchemaName = tempSchemaName,
			mainUserName = username
		)
	}

	private def getDataSourceConfig(host: String, port: String, sid: String, username: String, password: String, poolSize: Int): HikariConfig = {
		val connector = ""
		val config = new HikariConfig()
		println("username: " + username + " max pool size: " + poolSize)
		config.setJdbcUrl(s"jdbc:oracle:thin:@$connector?TNS_ADMIN=conf/private/oracle-wallet")
		config.setUsername(username)
		config.setPassword(password)

		// other options
		config.setMaximumPoolSize(poolSize)
		config.setLeakDetectionThreshold(30 * 1000)

		config
	}
}
