package org.sailcbi.APIServer.IO.PreparedQueries

import java.sql.ResultSet

import org.sailcbi.APIServer.Services.Authentication.UserType
import org.sailcbi.APIServer.Services.ResultSetWrapper

abstract class HardcodedQueryForSelect[T](
	override val allowedUserTypes: Set[UserType],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	implicit def wrapResultSet(rs: ResultSet): ResultSetWrapper = ResultSetWrapper.wrapResultSet(rs)

	def mapResultSetRowToCaseObject(rs: ResultSet): T
}