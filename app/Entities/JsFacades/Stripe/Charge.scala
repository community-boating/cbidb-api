package Entities.JsFacades.Stripe

import CbiUtil.GetSQLLiteral
import Entities.StorableJSObject
import play.api.libs.json.{JsValue, Json}

case class Charge(
  id: String,
  amount: Int,
  metadata: ChargeMetadata,
  created: Int,
  paid: Boolean,
  status: String
) extends StorableJSObject {
  val self = this
  val apexTableName = "STRIPE_CHARGES"
  val persistenceFields: Map[String, String] = Map(
    "CHARGE_ID" -> GetSQLLiteral(id),
    "AMOUNT_IN_CENTS" -> GetSQLLiteral(amount),
    "CREATED_EPOCH" -> GetSQLLiteral(created),
    "PAID" -> GetSQLLiteral(paid),
    "STATUS" -> GetSQLLiteral(status),
    "CLOSE_ID" -> GetSQLLiteral(metadata.closeId),
    "ORDER_ID" -> GetSQLLiteral(metadata.orderId),
    "TOKEN" -> GetSQLLiteral(metadata.token),
    "REFUNDS" -> GetSQLLiteral(metadata.refunds),
  )
  val pkColumnName = "CHARGE_ID"
  val pkSqlLiteral: String = GetSQLLiteral(id)

  // e.g. re_abc%123%1000&re_def%456%2000  => refund # re_abc, close 123, refund 1000 cents; refund # re_def, close 456, refund 2000 cents
  def refunds: List[ChargeRefund] = {
    val outerSeparator = "&"
    val innerSeparator = "%"
    metadata.refunds match {
      case None => List.empty
      case Some(s) => s.split(outerSeparator)
        .toList
        .map(_.split(innerSeparator))
        .map(p => ChargeRefund(p(0), id, p(1).toInt, p(2).toInt))
    }
  }
}

object Charge {
  implicit val chargeJSONFormat = Json.format[Charge]
  def apply(v: JsValue): Charge = v.as[Charge]
}

/*
{
    "id": "ch_1BmZTqIvhpK9IRKVamkZAdVM",
    "object": "charge",
    "amount": 1234,
    "amount_refunded": 0,
    "application": null,
    "application_fee": null,
    "balance_transaction": "txn_1BmZTqIvhpK9IRKVNKtV9U7N",
    "captured": true,
    "created": 1516505210,
    "currency": "usd",
    "customer": null,
    "description": "Test1",
    "destination": null,
    "dispute": null,
    "failure_code": null,
    "failure_message": null,
    "fraud_details": {},
    "invoice": null,
    "livemode": false,
    "metadata": {},
    "on_behalf_of": null,
    "order": null,
    "outcome": {
        "network_status": "approved_by_network",
        "reason": null,
        "risk_level": "normal",
        "seller_message": "Payment complete.",
        "type": "authorized"
    },
    "paid": true,
    "receipt_email": null,
    "receipt_number": null,
    "refunded": false,
    "refunds": {
        "object": "list",
        "data": [],
        "has_more": false,
        "total_count": 0,
        "url": "/v1/charges/ch_1BmZTqIvhpK9IRKVamkZAdVM/refunds"
    },
    "review": null,
    "shipping": null,
    "source": {
        "id": "card_1BkktdIvhpK9IRKVNuXHXVTP",
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
        "customer": null,
        "cvc_check": null,
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
    "source_transfer": null,
    "statement_descriptor": null,
    "status": "succeeded",
    "transfer_group": null
}
 */
