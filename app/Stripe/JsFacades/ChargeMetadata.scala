package Stripe.JsFacades

import play.api.libs.json.{JsValue, Json}

case class ChargeMetadata(
  closeId: Option[String],
  orderId: Option[String],
  token: Option[String],
  cbiInstance: Option[String]
)

object ChargeMetadata {
  implicit val chargeJSONFormat = Json.format[ChargeMetadata]
  def apply(v: JsValue): ChargeMetadata = v.as[ChargeMetadata]
}
