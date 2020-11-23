package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import play.api.libs.json.{JsValue, Json}

case class PaymentIntent(
	id: String,
	amount: Int,
	payment_method: Option[String],
	charges: ChargeList
)


case class ChargeList(data: List[Charge])

object ChargeList{
	implicit val chargeListJSONFormat = Json.format[ChargeList]
	def apply(v: JsValue): ChargeList = v.as[ChargeList]
}

object PaymentIntent {
	implicit val paymentIntentJSONFormat = Json.format[PaymentIntent]
	def apply(v: JsValue): PaymentIntent = v.as[PaymentIntent]
}