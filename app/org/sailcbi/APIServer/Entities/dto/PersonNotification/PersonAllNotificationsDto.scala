package org.sailcbi.APIServer.Entities.dto.PersonNotification

import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer

case class PersonAllNotificationsDto (
	yellowAp: PersonNotificationDto,
	redAp: PersonNotificationDto
)

object PersonAllNotificationsDto {
	implicit val format = Json.format[PersonAllNotificationsDto]
	def apply(v: JsValue): PersonAllNotificationsDto = v.as[PersonAllNotificationsDto]

	object events {
		val YELLOW_AP = "yellow-ap"
		val RED_AP = "red-ap"
	}
	object methods {
		val EMAIL = "email"
	}

	private def findMethods(dbRecords: List[PersonNotificationDbRecord], event: String) = PersonNotificationDto(
		email=dbRecords.exists(r => r.notificationEvent == event && r.notificationMethod == PersonAllNotificationsDto.methods.EMAIL)
	)

	def mapRecordsToDto(dbRecords: List[PersonNotificationDbRecord]): PersonAllNotificationsDto = PersonAllNotificationsDto(
		yellowAp = findMethods(dbRecords, PersonAllNotificationsDto.events.YELLOW_AP),
		redAp = findMethods(dbRecords, PersonAllNotificationsDto.events.RED_AP)
	)

	private def getMethods(event: String, methodsDto: PersonNotificationDto): List[PersonNotificationDbRecord] = {
		val ret: ListBuffer[PersonNotificationDbRecord] = ListBuffer()
		if (methodsDto.email) ret += PersonNotificationDbRecord(notificationEvent = event, notificationMethod = methods.EMAIL)
		ret.result
	}

	def mapDtoToRecords(dto: PersonAllNotificationsDto): List[PersonNotificationDbRecord] = {
		getMethods(events.YELLOW_AP, dto.yellowAp) ++ getMethods(events.RED_AP, dto.redAp)
	}
}