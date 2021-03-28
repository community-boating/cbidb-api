package com.coleji.framework.IO.PreparedQueries

import com.coleji.framework.Core.RequestCacheObject
import com.coleji.framework.Storable.ResultSetWrapper

abstract class HardcodedQueryForSelect[T](
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T
}
