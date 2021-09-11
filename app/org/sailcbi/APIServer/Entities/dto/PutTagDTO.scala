package org.sailcbi.APIServer.Entities.dto

import com.coleji.framework.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.Tag
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

case class PutTagDTO (
	TAG_ID: Option[Int],
	TAG_NAME: String,
) extends DTOClass[Tag] {
	override def getId: Option[Int] = TAG_ID

	override def mutateStorableForUpdate(s: Tag): Tag = {
		s.update(_.tagName, TAG_NAME)
		s
	}

	override def mutateStorableForInsert(s: Tag): Tag = mutateStorableForUpdate(s)
}

object PutTagDTO {
	implicit val format = Json.format[PutTagDTO]

	def apply(v: JsValue): PutTagDTO = v.as[PutTagDTO]
}
