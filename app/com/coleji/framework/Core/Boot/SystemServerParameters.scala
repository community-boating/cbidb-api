package com.coleji.framework.Core.Boot

import com.coleji.framework.Core.PermissionsAuthority.PersistenceSystem
import com.coleji.framework.Core.RequestCacheObject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class SystemServerParameters(
	entityPackagePath: String,
	serverTimeOffsetSeconds: Long,
	isTestMode: Boolean,
	isDebugMode: Boolean,
	readOnlyDatabase: Boolean,
	allowableUserTypes: List[RequestCacheObject[_]],
	preparedQueriesOnly: Boolean,
	persistenceSystem: PersistenceSystem
) {
	def nowDateTime: LocalDateTime = LocalDateTime.now.minusSeconds(serverTimeOffsetSeconds)
	val dateTimeFormatDefault: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
	def nowDateTimeString: String = nowDateTime.format(dateTimeFormatDefault)
}
