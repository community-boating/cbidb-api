package org.sailcbi.APIServer.Api.Endpoints.Kiosk

import play.api.libs.json.{JsValue, Json}

case class CreatePersonParams(
	firstName: String,
	lastName: String,
	emailAddress: String,
	dob: String,
	phonePrimary: String,
	phonePrimaryType: Option[String],
	emerg1Name: String,
	emerg1PhonePrimary: String,
	emerg1PhonePrimaryType: Option[String],
	emerg1Relation: Option[String],
	previousMember: Boolean,
	forRental: Option[Boolean],
)

object CreatePersonParams {
	implicit val createPersonParams = Json.format[CreatePersonParams]

	def apply(v: JsValue): CreatePersonParams = v.as[CreatePersonParams]
}