package com.coleji.framework.IO.PreparedQueries

import com.coleji.framework.Core.RequestCacheObject

abstract class HardcodedQueryForUpdateOrDelete(
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {

}
