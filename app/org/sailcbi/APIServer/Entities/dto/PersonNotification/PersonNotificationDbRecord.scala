package org.sailcbi.APIServer.Entities.dto.PersonNotification

import play.api.libs.json.{JsValue, Json}

case class PersonNotificationDbRecord(
	notificationEvent: String,
	notificationMethod: String
)

object PersonNotificationDbRecord {
	implicit val format = Json.format[PersonNotificationDbRecord]
	def apply(v: JsValue): PersonNotificationDbRecord = v.as[PersonNotificationDbRecord]
}