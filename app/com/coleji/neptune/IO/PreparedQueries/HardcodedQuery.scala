package com.coleji.neptune.IO.PreparedQueries

import com.coleji.neptune.Core.RequestCacheObject

abstract class HardcodedQuery(
	val allowedUserTypes: Set[RequestCacheObject[_]],
	val useTempSchema: Boolean = false
) {
	def getQuery: String
}
