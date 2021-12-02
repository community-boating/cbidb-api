package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.PersonMembership
import play.api.libs.json.{JsValue, Json}


case class PutPersonMembershipDTO (
	ASSIGN_ID: Option[Int],
	PERSON_ID: Int,
) extends DTOClass[PersonMembership] {
	override def getId: Option[Int] = ASSIGN_ID

	override def mutateStorableForUpdate(s: PersonMembership): PersonMembership = {
		s.update(_.personId, PERSON_ID)
		s
	}

	override def mutateStorableForInsert(s: PersonMembership): PersonMembership = mutateStorableForUpdate(s)
}

object PutPersonMembershipDTO {
	implicit val format = Json.format[PutPersonMembershipDTO]

	def apply(v: JsValue): PutPersonMembershipDTO = v.as[PutPersonMembershipDTO]
}
