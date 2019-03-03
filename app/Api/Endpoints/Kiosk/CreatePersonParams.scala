package Api.Endpoints.Kiosk

import play.api.libs.json.{JsValue, Json}

case class CreatePersonParams (
  nameFirst: String,
  nameLast: String,
  email: String,
  dob: String
)

object CreatePersonParams {
  implicit val paramsFormat = Json.format[CreatePersonParams]
  def apply(v: JsValue): CreatePersonParams = v.as[CreatePersonParams]
}