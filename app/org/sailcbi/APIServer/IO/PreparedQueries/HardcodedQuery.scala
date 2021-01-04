package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserTypeObject

abstract class HardcodedQuery(
	val allowedUserTypes: Set[UserTypeObject[_]],
	val useTempSchema: Boolean = false
) {
	def getQuery: String
}
