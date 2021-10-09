package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.HighSchool
import play.api.libs.json.{JsValue, Json}

case class PutHighSchoolDTO (
	SCHOOL_ID: Option[Int],
	SCHOOL_NAME: String,
	ACTIVE: Boolean,
) extends DTOClass[HighSchool] {
	override def getId: Option[Int] = SCHOOL_ID

	override def mutateStorableForUpdate(s: HighSchool): HighSchool = {
		s.update(_.schoolName, SCHOOL_NAME)
		s.update(_.active, ACTIVE)
		s
	}

	override def mutateStorableForInsert(s: HighSchool): HighSchool = mutateStorableForUpdate(s)
}

object PutHighSchoolDTO {
	implicit val format = Json.format[PutHighSchoolDTO]

	def apply(v: JsValue): PutHighSchoolDTO = v.as[PutHighSchoolDTO]
}
