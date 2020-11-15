package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import play.api.libs.json.{JsValue, Json}

case class CustomerInvoiceSettings (
	default_payment_method: Option[String]
)

object CustomerInvoiceSettings {
	implicit val format = Json.format[CustomerInvoiceSettings]

	def apply(v: JsValue): CustomerInvoiceSettings = v.as[CustomerInvoiceSettings]
}
