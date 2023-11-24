package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.GuestPriv
import play.api.libs.json.{JsValue, Json}

case class PutGuestPrivDTO (
	MEMBERSHIP_ID: Option[Int],
	PRICE: Option[Double]
) extends DTOClass[GuestPriv] {
	override def getId: Option[Int] = MEMBERSHIP_ID

	override def mutateStorableForUpdate(s: GuestPriv): GuestPriv = {
		s.update(_.price, PRICE.get)
		s
	}

	override def mutateStorableForInsert(s: GuestPriv): GuestPriv = mutateStorableForUpdate(s)
}

object PutGuestPrivDTO {
	implicit val format = Json.format[PutGuestPrivDTO]

	def apply(v: JsValue): PutGuestPrivDTO = v.as[PutGuestPrivDTO]
}