package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserTypeObject
import org.sailcbi.APIServer.Services.ResultSetWrapper

abstract class HardcodedQueryForSelect[T](
	override val allowedUserTypes: Set[UserTypeObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T
}
