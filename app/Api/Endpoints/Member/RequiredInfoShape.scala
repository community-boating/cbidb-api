package Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}

case class RequiredInfoShape(
	personId: Int,
	firstName: Option[String],
	lastName: Option[String],
	middleInitial: Option[String],
	dob: Option[String],
	childEmail: Option[String],
	addr1: Option[String],
	addr2: Option[String],
	addr3: Option[String],
	city: Option[String],
	state: Option[String],
	zip: Option[String],
	country: Option[String],
	primaryPhone: Option[String],
	primaryPhoneType: Option[String],
	alternatePhone: Option[String],
	alternatePhoneType: Option[String],
	allergies: Option[String],
	medications: Option[String],
	specialNeeds: Option[String]
)

object RequiredInfoShape {
	implicit val format = Json.format[RequiredInfoShape]

	def apply(v: JsValue): RequiredInfoShape = v.as[RequiredInfoShape]
}
