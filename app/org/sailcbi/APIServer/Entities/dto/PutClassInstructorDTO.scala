package org.sailcbi.APIServer.Entities.dto

import play.api.libs.json.{JsValue, Json}

case class PutClassInstructorDTO(
	instructorId: Option[Int],
	nameFirst: String,
	nameLast: String,
)

object PutClassInstructorDTO {
	implicit val format = Json.format[PutClassInstructorDTO]

	def apply(v: JsValue): PutClassInstructorDTO = v.as[PutClassInstructorDTO]
}
