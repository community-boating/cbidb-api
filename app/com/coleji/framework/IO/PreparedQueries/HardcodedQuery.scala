package com.coleji.framework.IO.PreparedQueries

import org.sailcbi.APIServer.Services.RequestCacheObject

abstract class HardcodedQuery(
	val allowedUserTypes: Set[RequestCacheObject[_]],
	val useTempSchema: Boolean = false
) {
	def getQuery: String
}
