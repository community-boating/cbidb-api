package IO.PreparedQueries

import play.api.libs.json.JsArray

trait CastableToJSObject[T] {
  def mapCaseObjectToJsArray(caseObject: T): JsArray
}
