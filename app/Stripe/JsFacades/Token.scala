package Stripe.JsFacades

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Token(
  id: String,
  card: Card,
  created: Int,
  used: Boolean
)

object Token {
  implicit val tokenReads = Json.format[Token]
}

/*
{
  "id": "tok_1BozTPIvhpK9IRKVU9m2n5xF",
  "object": "token",
  "card": {
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
  "client_ip": "73.167.60.100",
  "created": 1517081783,
  "livemode": false,
  "type": "card",
  "used": true
}
*/