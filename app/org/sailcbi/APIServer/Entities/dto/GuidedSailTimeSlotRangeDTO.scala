package org.sailcbi.APIServer.Entities.dto

import play.api.libs.json.Json

case class GuidedSailTimeSlotRangeDTO(startDatetime: String, endDatetime: String) {
}

object GuidedSailTimeSlotRangeDTO {
  implicit val format = Json.format[GuidedSailTimeSlotRangeDTO]
}