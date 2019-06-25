package Services

import java.sql.Connection

import CbiUtil.Profiler
import com.zaxxer.hikari.HikariDataSource

class ConnectionPoolWrapper(private val source: HikariDataSource) {
	def withConnection[T](block: Connection => T): T = {
		val c: Connection = source.getConnection()
		try {
			println("starting block")
			val ret = block(c)
			println("finished block")
			ret
		} finally {
			println("about to close connection")
			c.close()
			println("closed connection")
		}
	}
}
