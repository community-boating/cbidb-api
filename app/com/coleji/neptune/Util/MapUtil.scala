package com.coleji.neptune.Util

object MapUtil {
	def getOrError(map: Map[String, String], field: String): Either[String, String] = getOrError(map, field, x => x)
	def getOrError(map: Map[String, String], field: String, fieldMapper: String => String): Either[String, String] =
		map.get(field).map(Right(_)).getOrElse(Left(fieldMapper(field)))
}
