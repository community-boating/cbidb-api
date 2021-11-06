package org.sailcbi.APIServer.Entities.dto

import com.coleji.neptune.Storable.DTOClass
import org.sailcbi.APIServer.Entities.EntityDefinitions.DonationFund
import play.api.libs.json.{JsValue, Json}

case class PutDonationFundDTO(
	FUND_ID: Option[Int],
	FUND_NAME: String,
	ACTIVE: Boolean,
	DISPLAY_ORDER: Option[Double],
	LETTER_TEXT: Option[String],
	SHOW_IN_CHECKOUT: Boolean,
	PORTAL_DESCRIPTION: Option[String],
	IS_ENDOWMENT: Boolean
) extends DTOClass[DonationFund] {
	override def getId: Option[Int] = FUND_ID

	override def mutateStorableForUpdate(s: DonationFund): DonationFund = {
		s.update(_.fundName, FUND_NAME)
		s.update(_.active, ACTIVE)
		s.update(_.displayOrder, DISPLAY_ORDER)
		s.update(_.letterText, LETTER_TEXT)
		s.update(_.showInCheckout, SHOW_IN_CHECKOUT)
		s.update(_.portalDescription, PORTAL_DESCRIPTION)
		s.update(_.isEndowment, IS_ENDOWMENT)
		s
	}

	override def mutateStorableForInsert(s: DonationFund): DonationFund = mutateStorableForUpdate(s)
}

object PutDonationFundDTO {
	implicit val format = Json.format[PutDonationFundDTO]

	def apply(v: JsValue): PutDonationFundDTO = v.as[PutDonationFundDTO]
}
