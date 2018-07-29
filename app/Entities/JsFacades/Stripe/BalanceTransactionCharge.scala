package Entities.JsFacades.Stripe

import play.api.libs.json.{JsString, JsValue, Json}

case class BalanceTransactionCharge(
  id: String,
  amount: Int,
  description: String,
  fee: Int,
  net: Int,
  source: String,
  `type`: String
) extends BalanceTransaction {
  def getId: String = id
}

object BalanceTransactionCharge {
  implicit val balanceTransactionChargeJSONFormat = Json.format[BalanceTransactionCharge]
  def apply(v: JsValue): BalanceTransactionCharge = v.as[BalanceTransactionCharge]
}