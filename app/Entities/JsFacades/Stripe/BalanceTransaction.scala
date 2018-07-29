package Entities.JsFacades.Stripe

import play.api.libs.json.JsValue

trait BalanceTransaction {
  def getId: String
}

object BalanceTransaction {
  def apply(v: JsValue): BalanceTransaction = {
    v("type").as[String] match {
      case "charge" => BalanceTransactionCharge(v)
      case "payout" => BalanceTransactionPayout(v)
      case "refund" => BalanceTransactionRefund(v)
      case s: String => throw new Exception("Unrecognized balance transaction type " + s + "\n" + v.toString())
    }
  }
}