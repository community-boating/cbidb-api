package Entities.JsFacades.Stripe

import play.api.libs.json.Json

case class Card (
  id: String,
  address_zip: Option[String],
  exp_month: Int,
  exp_year: Int,
  last4: String
)

object Card {
  implicit val cardReads = Json.format[Card]
}

/*
{
  "id": "card_1BozTPIvhpK9IRKVJENlcrX6",
  "object": "card",
  "address_city": null,
  "address_country": null,
  "address_line1": null,
  "address_line1_check": null,
  "address_line2": null,
  "address_state": null,
  "address_zip": "02138",
  "address_zip_check": "pass",
  "brand": "Visa",
  "country": "US",
  "cvc_check": "pass",
  "dynamic_last4": null,
  "exp_month": 11,
  "exp_year": 2021,
  "fingerprint": "gTe1Kzkic12Sst5a",
  "funding": "unknown",
  "last4": "1111",
  "metadata": {},
  "name": null,
  "tokenization_method": null
},
 */