package com.coleji.framework.Core

import com.zaxxer.hikari.HikariDataSource

import java.sql.Connection

class ConnectionPoolWrapper(private val source: HikariDataSource)  {
	private[Core] def withConnection[T](block: Connection => T)(implicit PA: PermissionsAuthority): T = {
		var c: Connection = null
		try {
			c = source.getConnection()
			val ret = block(c)
			ret
		} catch {
			case e: Throwable => {
				PA.logger.error("Error using a DB connection: ", e)
				throw e
			}
		} finally {
			c.close()
		}
	}

	private[Core] def close(): Unit = source.close()
}
