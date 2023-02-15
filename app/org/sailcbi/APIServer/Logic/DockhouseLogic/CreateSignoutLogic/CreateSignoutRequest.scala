package org.sailcbi.APIServer.Logic.DockhouseLogic.CreateSignoutLogic

import play.api.libs.json.{JsValue, Json}

case class CreateSignoutRequest(
	skipperPersonId: Int,
	programId: Int,
	skipperCardNumber: String,
	skipperTestRatingId: Option[Int],
	boatId: Int,
	sailNumber: Option[String],
	hullNumber: Option[String],
	classSessionId: Option[Int],
	isRacing: Boolean,
	dockmasterOverride: Boolean,
	didInformKayakRules: Boolean,
	signoutCrew: List[CreateSignoutCrewRequest]
) {
	def toJson: JsValue = {
		implicit val successFormat = CreateSignoutRequest.format
		Json.toJson(this)
	}
}

object CreateSignoutRequest {
	implicit val crewFormat = Json.format[CreateSignoutCrewRequest]
	implicit val format = Json.format[CreateSignoutRequest]

	def apply(v: JsValue): CreateSignoutRequest = v.as[CreateSignoutRequest]
}
