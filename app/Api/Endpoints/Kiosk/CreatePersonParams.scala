package Api.Endpoints.Kiosk

import play.api.libs.json.{JsValue, Json}

case class CreatePersonParams (
  firstName: String,
  lastName: String,
  emailAddress: String,
  dob: String,
  phonePrimary: String,
  emerg1Name: String,
  emerg1Relation: String,
  emerg1PhonePrimary: String,
  previousMember: Boolean
)

object CreatePersonParams {
  implicit val createPersonParams = Json.format[CreatePersonParams]
  def apply(v: JsValue): CreatePersonParams = v.as[CreatePersonParams]
}