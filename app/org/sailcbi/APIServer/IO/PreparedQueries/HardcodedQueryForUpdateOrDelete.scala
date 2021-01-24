package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.RequestCacheObject

abstract class HardcodedQueryForUpdateOrDelete(
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {

}
