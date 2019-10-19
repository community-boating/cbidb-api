package org.sailcbi.APIServer.Entities.Misc

import play.api.libs.json.{JsValue, Json}

case class StripeTokenSavedShape (token: String, orderId: Int, last4: String, expMonth: String, expYear: String, zip: Option[String])

object StripeTokenSavedShape {
	implicit val format = Json.format[StripeTokenSavedShape]

	def apply(v: JsValue): StripeTokenSavedShape = v.as[StripeTokenSavedShape]
}
