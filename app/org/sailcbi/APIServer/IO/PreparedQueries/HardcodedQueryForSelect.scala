package org.sailcbi.APIServer.IO.PreparedQueries

import java.sql.ResultSet

import org.sailcbi.APIServer.Services.Authentication.UserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

abstract class HardcodedQueryForSelect[T](
	override val allowedUserTypes: Set[UserType],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T
}
