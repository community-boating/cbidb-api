package com.coleji.neptune.Core

import com.zaxxer.hikari.HikariDataSource

import java.sql.Connection
import java.util.concurrent.atomic.AtomicInteger

class ConnectionPoolWrapper(private val source: HikariDataSource)  {
	var openConnections = new AtomicInteger(0)
	var connectionHighWaterMark = new AtomicInteger(0)

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
		openConnections.incrementAndGet()
		if (openConnections.get() > connectionHighWaterMark.get()) {
			connectionHighWaterMark.set(openConnections.get())
		}
		println("Grabbed DB connection; in use: " + openConnections.get())
		println("high water mark: " + connectionHighWaterMark.get())
	}

	private def decrement(): Unit = {
		openConnections.decrementAndGet()
		println("Freed DB connection; in use: " + openConnections.get())
	}

	private[Core] def close(): Unit = source.close()
}
