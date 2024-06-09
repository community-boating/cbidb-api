package org.sailcbi.APIServer.Entities.dto

import play.api.libs.json.{Json, OFormat}

case class GuidedSailTimeSlotsForDayDTO(day: String, slots: List[(String, String)]) {
}

object GuidedSailTimeSlotsForDayDTO {
  implicit val format: OFormat[GuidedSailTimeSlotsForDayDTO] = Json.format[GuidedSailTimeSlotsForDayDTO]
}

case class GuidedSailCurrentSessionDTO(startDatetime: String, sessionLength: Double, instanceId: Int, signupCt: Int, maxSignups: Int) {
}

object GuidedSailCurrentSessionDTO {
  implicit val format: OFormat[GuidedSailCurrentSessionDTO] = Json.format[GuidedSailCurrentSessionDTO]
}

case class GuidedSailCreateInstanceResult(instanceId: Option[Int], signupCt: Option[Int], errors: Option[String]) {
}

object GuidedSailCreateInstanceResult {
  implicit val format: OFormat[GuidedSailCreateInstanceResult] = Json.format[GuidedSailCreateInstanceResult]
}