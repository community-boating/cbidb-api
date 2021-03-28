package com.coleji.framework.IO.PreparedQueries

import com.coleji.framework.Core.RequestCacheObject
import play.api.libs.json.{JsArray, JsObject, JsString}

abstract class HardcodedQueryForSelectCastableToJSObject[T](
	override val allowedUserTypes: Set[RequestCacheObject[_]],
	override val useTempSchema: Boolean = false
) extends HardcodedQueryForSelect[T](allowedUserTypes, useTempSchema) with CastableToJSObject[T] {
	val columnNames: List[String]

	def getColumnsNamesAsJSObject: JsArray = JsArray(columnNames.map(n => JsObject(Map(
		"name" -> JsString(n)
	))))
}
