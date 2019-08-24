package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserType

abstract class HardcodedQuery(
	val allowedUserTypes: Set[UserType],
	val useTempSchema: Boolean = false
) {
	def getQuery: String
}
