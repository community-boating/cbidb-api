package org.sailcbi.APIServer.Services

import java.sql.Connection

import org.sailcbi.APIServer.CbiUtil.Profiler
import com.zaxxer.hikari.HikariDataSource

class ConnectionPoolWrapper(private val source: HikariDataSource)  {
	def withConnection[T](block: Connection => T)(implicit PA: PermissionsAuthority): T = {
		var c: Connection = null
		try {
			c = source.getConnection()
			println("starting block")
			val ret = block(c)
			println("finished block")
			ret
		} catch {
			case e: Throwable => {
				PA.logger.error("Error using a DB connection: ", e)
				throw e
			}
		} finally {
			println("about to close connection")
			c.close()
			println("closed connection")
		}
	}

	def close(): Unit = source.close()
}
