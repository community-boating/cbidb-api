package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.CreateSignout

import play.api.libs.json.{JsObject, Json}

case class CreateSignoutError(code: String, message: String, extras: List[CreateSignoutSingleError]) {
	implicit val crewFormat = Json.format[CreateSignoutSingleError]
	implicit val format = Json.format[CreateSignoutError]
	def asJsObject: JsObject = JsObject(Map("error" -> Json.toJson(this)))
}

case class CreateSignoutSingleError(code: String, message: String, overridable: Boolean)
