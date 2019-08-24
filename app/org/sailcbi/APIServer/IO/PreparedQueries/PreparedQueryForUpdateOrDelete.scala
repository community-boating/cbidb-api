package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserType

abstract class PreparedQueryForUpdateOrDelete(
	override val allowedUserTypes: Set[UserType],
	override val useTempSchema: Boolean = false
) extends HardcodedQueryForUpdateOrDelete(allowedUserTypes, useTempSchema) {
	val params: List[String]
}
