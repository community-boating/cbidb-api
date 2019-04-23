package Api.Endpoints.Member

import play.api.libs.json.{JsValue, Json}

case class RequiredInfoShape(
  firstName: String,
  lastName: String,
  middleInitial: String,
  dob: String,
  childEmail: String,
  addr1: String,
  addr2: String,
  addr3: String,
  city: String,
  state: String,
  zip: String,
  country: String,
  primaryPhone: String,
  primaryPhoneType: String,
  alternatePhone: String,
  alternatePhoneType: String,
  allergies: String,
  medications: String,
  specialNeeds: String
)

object RequiredInfoShape {
  implicit val format = Json.format[RequiredInfoShape]
  def apply(v: JsValue): RequiredInfoShape = v.as[RequiredInfoShape]
}
