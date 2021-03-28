package com.coleji.framework.IO.PreparedQueries

import org.sailcbi.APIServer.Services.{RequestCacheObject, ResultSetWrapper}

abstract class HardcodedQueryForSelect[T](
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQuery(allowedUserTypes, useTempSchema) {
	def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): T
}
