package Api.Endpoints.Kiosk

import play.api.libs.json.{JsValue, Json}

case class CreatePersonParams (
  firstName: String,
  lastName: String,
  emailAddress: String,
  dob: String
)

object CreatePersonParams {
  implicit val paramsFormat = Json.format[CreatePersonParams]
  def apply(v: JsValue): CreatePersonParams = v.as[CreatePersonParams]
}