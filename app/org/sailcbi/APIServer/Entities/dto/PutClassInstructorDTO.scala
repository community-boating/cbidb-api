package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.ClassInstructor
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
