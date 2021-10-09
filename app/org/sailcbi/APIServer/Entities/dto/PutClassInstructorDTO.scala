package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.ClassInstructor
import play.api.libs.json.{JsValue, Json}

case class PutClassInstructorDTO(
	INSTRUCTOR_ID: Option[Int],
	NAME_FIRST: String,
	NAME_LAST: String,
) extends DTOClass[ClassInstructor] {
	override def getId: Option[Int] = INSTRUCTOR_ID

	override def mutateStorableForUpdate(s: ClassInstructor): ClassInstructor = {
		s.update(_.nameFirst, NAME_FIRST)
		s.update(_.nameLast, NAME_LAST)
		s
	}

	override def mutateStorableForInsert(s: ClassInstructor): ClassInstructor = mutateStorableForUpdate(s)
}

object PutClassInstructorDTO {
	implicit val format = Json.format[PutClassInstructorDTO]

	def apply(v: JsValue): PutClassInstructorDTO = v.as[PutClassInstructorDTO]
}
