package IO.PreparedQueries

import Services.Authentication.UserType
import play.api.libs.json.{JsArray, JsObject, JsString}

abstract class HardcodedQueryForSelectCastableToJSObject[T](
																   override val allowedUserTypes: Set[UserType],
																   override val useTempSchema: Boolean = false
														   ) extends HardcodedQueryForSelect[T](allowedUserTypes, useTempSchema) with CastableToJSObject[T] {
	val columnNames: List[String]

	def getColumnsNamesAsJSObject: JsArray = JsArray(columnNames.map(n => JsObject(Map(
		"name" -> JsString(n)
	))))
}
