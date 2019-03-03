package Api.Endpoints.Kiosk

import play.api.libs.json.{JsValue, Json}

case class CreateCardParams (
  personID: Int
)

object CreateCardParams {
  implicit val ceateCardParamsFormat = Json.format[CreateCardParams]
  def apply(v: JsValue): CreateCardParams = v.as[CreateCardParams]
}