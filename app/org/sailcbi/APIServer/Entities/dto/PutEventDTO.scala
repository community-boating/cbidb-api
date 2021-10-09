package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.Event
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutEventDTO (
	EVENT_ID: Option[Int],
	EVENT_NAME: String,
	EVENT_DATE: Option[LocalDateTime],
) extends DTOClass[Event] {
	override def getId: Option[Int] = EVENT_ID

	override def mutateStorableForUpdate(s: Event): Event = {
		s.update(_.eventName, EVENT_NAME)
		s.update(_.eventDateTime, EVENT_DATE)
		s
	}

	override def mutateStorableForInsert(s: Event): Event = mutateStorableForUpdate(s)
}

object PutEventDTO {
	implicit val format = Json.format[PutEventDTO]

	def apply(v: JsValue): PutEventDTO = v.as[PutEventDTO]
}
