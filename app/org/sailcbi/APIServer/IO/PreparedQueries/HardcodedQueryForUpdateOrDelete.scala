package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.UserTypeObject

abstract class HardcodedQueryForUpdateOrDelete(
	override val allowedUserTypes: Set[UserTypeObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {

}
