package org.sailcbi.APIServer.Server

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class ServerParameters(
							   serverTimeOffsetSeconds: Long
						   ) {
	def nowDateTime: LocalDateTime = LocalDateTime.now.minusSeconds(serverTimeOffsetSeconds)

	val dateTimeFormatDefault: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

	def nowDateTimeString: String = nowDateTime.format(dateTimeFormatDefault)
}
