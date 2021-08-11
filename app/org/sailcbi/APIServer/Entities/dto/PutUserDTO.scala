package org.sailcbi.APIServer.Entities.dto

import com.coleji.framework.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.User
import org.sailcbi.APIServer.Entities.MagicIds
import play.api.libs.json.{JsValue, Json}

case class PutUserDTO(
	USER_ID: Option[Int],
	USER_NAME: String,
	NAME_FIRST: Option[String],
	NAME_LAST: Option[String],
	EMAIL: String,
	ACTIVE: Boolean,
	HIDE_FROM_CLOSE: Boolean,
	LOCKED: Boolean,
	PW_CHANGE_REQD: Boolean,
	USER_TYPE: Option[String],
	pwHash: Option[String],
) extends DTOClass[User] {
	override def getId: Option[Int] = USER_ID

	override def mutateStorableForUpdate(s: User): User = {
		s.update(_.email, EMAIL)
		s.update(_.nameFirst, NAME_FIRST)
		s.update(_.nameLast, NAME_LAST)
		s.update(_.active, ACTIVE)
		s.update(_.pwChangeRequired, PW_CHANGE_REQD)
		s.update(_.locked, LOCKED)
		s.update(_.hideFromClose, HIDE_FROM_CLOSE)
		s.update(_.userType, USER_TYPE)
		if (pwHash.isDefined) {
			s.update(_.pwHash, pwHash)
			s.update(_.pwHashScheme, Some(MagicIds.PW_HASH_SCHEME.STAFF_2))
		}
		s
	}

	override def mutateStorableForInsert(s: User): User = {
		mutateStorableForUpdate(s)
		s.update(_.userName, USER_NAME)
		s
	}
}

object PutUserDTO {
	implicit val format = Json.format[PutUserDTO]

	def apply(v: JsValue): PutUserDTO = v.as[PutUserDTO]
}
