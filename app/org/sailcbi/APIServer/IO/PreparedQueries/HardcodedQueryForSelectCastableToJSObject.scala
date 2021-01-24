package org.sailcbi.APIServer.IO.PreparedQueries

import org.sailcbi.APIServer.Services.Authentication.RequestCacheObject
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
