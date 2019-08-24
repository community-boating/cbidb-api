package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserType

abstract class PreparedQueryForInsert(
	override val allowedUserTypes: Set[UserType],
	override val useTempSchema: Boolean = false
) extends HardcodedQueryForInsert(allowedUserTypes, useTempSchema) {
	val params: List[String]
}
