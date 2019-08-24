package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserType

abstract class HardcodedQueryForInsert(
	override val allowedUserTypes: Set[UserType],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	val pkName: Option[String]
}
