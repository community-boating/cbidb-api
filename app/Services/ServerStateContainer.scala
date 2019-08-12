package Services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


case class ServerStateContainer private[Services](
	serverTimeOffsetSeconds: Long
) {
	def nowDateTime: LocalDateTime = LocalDateTime.now.minusSeconds(serverTimeOffsetSeconds)

	val dateTimeFormatDefault: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	def nowDateTimeString: String = nowDateTime.format(dateTimeFormatDefault)
}

object ServerStateContainer {
	def get: ServerStateContainer = ServerBootLoader.ssc
}