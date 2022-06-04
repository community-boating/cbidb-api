package org.sailcbi.APIServer.Entities.dto.PersonNotification

import play.api.libs.json.{JsValue, Json}

case class PersonNotificationDto(
	email: Boolean
)

object PersonNotificationDto {
	implicit val format = Json.format[PersonNotificationDto]
	def apply(v: JsValue): PersonNotificationDto = v.as[PersonNotificationDto]
}
