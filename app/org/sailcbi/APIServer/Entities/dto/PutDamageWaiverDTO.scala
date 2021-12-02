package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DamageWaiver
import play.api.libs.json.{JsValue, Json}

case class PutDamageWaiverDTO (
	WAIVER_ID: Option[Int],
	PERSON_ID: Int,
	PRICE: Option[Double],
) extends DTOClass[DamageWaiver] {
	override def getId: Option[Int] = WAIVER_ID

	override def mutateStorableForUpdate(s: DamageWaiver): DamageWaiver = {
		s.update(_.personId, PERSON_ID)
		s.update(_.price, PRICE)
		s
	}

	override def mutateStorableForInsert(s: DamageWaiver): DamageWaiver = mutateStorableForUpdate(s)
}

object PutDamageWaiverDTO {
	implicit val format = Json.format[PutDamageWaiverDTO]

	def apply(v: JsValue): PutDamageWaiverDTO = v.as[PutDamageWaiverDTO]
}
