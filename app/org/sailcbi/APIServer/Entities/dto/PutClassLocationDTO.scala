package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.ClassLocation
import play.api.libs.json.{JsValue, Json}

case class PutClassLocationDTO(
	LOCATION_ID: Option[Int],
	LOCATION_NAME: String,
	ACTIVE: Boolean,
) extends DTOClass[ClassLocation] {
	override def getId: Option[Int] = LOCATION_ID

	override def mutateStorableForUpdate(s: ClassLocation): ClassLocation = {
		s.update(_.locationName, LOCATION_NAME)
		s.update(_.active, ACTIVE)
		s
	}

	override def mutateStorableForInsert(s: ClassLocation): ClassLocation = mutateStorableForUpdate(s)
}

object PutClassLocationDTO {
	implicit val format = Json.format[PutClassLocationDTO]

	def apply(v: JsValue): PutClassLocationDTO = v.as[PutClassLocationDTO]
}
