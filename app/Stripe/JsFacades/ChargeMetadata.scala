package Stripe.JsFacades

import play.api.libs.json.{JsValue, Json}

case class ChargeMetadata(
  closeId: String,
  orderId: String,
  token: String,
  cbiInstance: String
)

object ChargeMetadata {
  implicit val chargeJSONFormat = Json.format[ChargeMetadata]
  def apply(v: JsValue): ChargeMetadata = v.as[ChargeMetadata]
}
