package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import play.api.libs.json.{JsValue, Json}

case class Customer(
	id: String,
	email: Option[String]
)

object Customer {
	implicit val customerFormat = Json.format[Customer]

	def apply(v: JsValue): Customer = v.as[Customer]
}

/*
{
  "id": "cus_HQJ5AbR7bqCNa4",
  "object": "customer",
  "address": null,
  "balance": 0,
  "created": 1591551587,
  "currency": "usd",
  "default_source": null,
  "delinquent": false,
  "description": "My First Test Customer (created for API docs)",
  "discount": null,
  "email": null,
  "invoice_prefix": "7E2C2AB",
  "invoice_settings": {
    "custom_fields": null,
    "default_payment_method": null,
    "footer": null
  },
  "livemode": false,
  "metadata": {},
  "name": null,
  "next_invoice_sequence": 1,
  "phone": null,
  "preferred_locales": [],
  "shipping": null,
  "sources": {
    "object": "list",
    "data": [],
    "has_more": false,
    "url": "/v1/customers/cus_HQJ5AbR7bqCNa4/sources"
  },
  "subscriptions": {
    "object": "list",
    "data": [],
    "has_more": false,
    "url": "/v1/customers/cus_HQJ5AbR7bqCNa4/subscriptions"
  },
  "tax_exempt": "none",
  "tax_ids": {
    "object": "list",
    "data": [],
    "has_more": false,
    "url": "/v1/customers/cus_HQJ5AbR7bqCNa4/tax_ids"
  }
}
 */