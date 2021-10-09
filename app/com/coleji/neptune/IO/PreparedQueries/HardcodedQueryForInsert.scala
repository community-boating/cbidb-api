package com.coleji.neptune.IO.PreparedQueries

import com.coleji.neptune.Core.RequestCacheObject

abstract class HardcodedQueryForInsert(
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	val pkName: Option[String]
}
