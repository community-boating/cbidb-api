package com.coleji.neptune.Core

import com.zaxxer.hikari.HikariDataSource

import java.sql.Connection

class ConnectionPoolWrapper(private val source: HikariDataSource)  {
	var openConnections = 0
	var connectionHighWaterMark = 0;

	private[Core] def withConnection[T](block: Connection => T)(implicit PA: PermissionsAuthority): T = {
		var c: Connection = null
		try {
			c = source.getConnection()
			increment()
			val ret = block(c)
			ret
		} catch {
			case e: Throwable => {
				PA.logger.error("Error using a DB connection: ", e)
				throw e
			}
		} finally {
			if (c != null) {
				c.close()
				decrement()
			}
		}
	}

	private def increment(): Unit = {
		openConnections += 1
		if (openConnections > connectionHighWaterMark) {
			connectionHighWaterMark = openConnections
		}
		println("Grabbed DB connection; in use: " + openConnections)
		println("high water mark: " + connectionHighWaterMark)
	}

	private def decrement(): Unit = {
		openConnections -= 1
		println("Freed DB connection; in use: " + openConnections)
	}

	private[Core] def close(): Unit = source.close()
}
