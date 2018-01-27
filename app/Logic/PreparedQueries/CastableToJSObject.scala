package Logic.PreparedQueries

import Logic.PreparedQueries.Public.GetApClassInstancesResult
import play.api.libs.json.JsArray

trait CastableToJSObject[T] {
  def mapCaseObjectToJsArray(caseObject: T): JsArray
}
