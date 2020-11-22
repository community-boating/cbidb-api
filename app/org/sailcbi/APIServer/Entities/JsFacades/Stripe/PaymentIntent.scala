package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import play.api.libs.json.{JsValue, Json}

case class PaymentIntent(
	id: String,
	amount: Int,
	payment_method: Option[String]
)

object PaymentIntent {
	implicit val paymentIntentJSONFormat = Json.format[PaymentIntent]

	def apply(v: JsValue): PaymentIntent = v.as[PaymentIntent]
}