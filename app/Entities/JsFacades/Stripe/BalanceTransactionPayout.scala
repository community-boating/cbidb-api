package Entities.JsFacades.Stripe

import play.api.libs.json.{JsValue, Json}

case class BalanceTransactionPayout(
  id: String,
  amount: Int,
  fee: Int,
  net: Int,
  source: String,
  status: String,
  `type`: String
) extends BalanceTransaction {
  def getId: String = id
}

object BalanceTransactionPayout {
  implicit val balanceTransactionPayoutJSONFormat = Json.format[BalanceTransactionPayout]
  def apply(v: JsValue): BalanceTransactionPayout = v.as[BalanceTransactionPayout]
}