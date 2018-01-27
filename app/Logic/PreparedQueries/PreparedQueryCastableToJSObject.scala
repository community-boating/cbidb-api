package Logic.PreparedQueries

import play.api.libs.json.{JsArray, JsObject, JsString}

abstract class PreparedQueryCastableToJSObject[T <: PreparedQueryCaseResult] extends PreparedQuery[T] with CastableToJSObject[T] {
  val columnNames: List[String]

  def getColumnsNamesAsJSObject: JsArray = JsArray(columnNames.map(n => JsObject(Map(
    "name" -> JsString(n)
  ))))
}
