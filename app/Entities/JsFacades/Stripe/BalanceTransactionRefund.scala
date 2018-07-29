package Entities.JsFacades.Stripe

import play.api.libs.json.{JsValue, Json}

case class BalanceTransactionRefund (
  id: String,
  amount: Int,
  description: String,
  fee: Int,
  net: Int,
  source: String,
  status: String,
  `type`: String
) extends BalanceTransaction {
  def getId: String = id
}

object BalanceTransactionRefund {
  implicit val balanceTransactionRefundJSONFormat = Json.format[BalanceTransactionRefund]
  def apply(v: JsValue): BalanceTransactionRefund = v.as[BalanceTransactionRefund]
}