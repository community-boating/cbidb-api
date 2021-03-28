package com.coleji.framework.IO.PreparedQueries

import com.coleji.framework.Core.RequestCacheObject

abstract class HardcodedQuery(
	val allowedUserTypes: Set[RequestCacheObject[_]],
	val useTempSchema: Boolean = false
) {
	def getQuery: String
}
