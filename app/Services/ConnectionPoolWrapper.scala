package Services

import java.sql.Connection

import CbiUtil.Profiler
import com.zaxxer.hikari.HikariDataSource

class ConnectionPoolWrapper(private val source: HikariDataSource) {
	def withConnection[T](block: Connection => T): T = {
		var c: Connection = null
		try {
			c = source.getConnection()
			println("starting block")
			val ret = block(c)
			println("finished block")
			ret
		} catch {
			case e: Throwable => {
				PermissionsAuthority.logger.error("Error using a DB connection: ", e)
				throw e
			}
		} finally {
			println("about to close connection")
			c.close()
			println("closed connection")
		}
	}
}
