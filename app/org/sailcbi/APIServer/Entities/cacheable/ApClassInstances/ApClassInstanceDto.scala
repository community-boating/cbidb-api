package org.sailcbi.APIServer.Entities.cacheable.ApClassInstances

import play.api.libs.json.{JsValue, Json}

case class ApClassInstanceDto(
	instanceId: Int,
	typeName: String,
	sessionDate: String,
	sessionTime: String,
	location: String,
	enrollees: Int
)

object ApClassInstanceDto {
	implicit val format = Json.format[ApClassInstanceDto]

	def apply(v: JsValue): ApClassInstanceDto = v.as[ApClassInstanceDto]
}