package Entities.JsFacades.Stripe

import CbiUtil.GetSQLLiteral
import Entities.StorableJSObject

case class ChargeRefund(
  refundId: String,
  chargeId: String,
  closeId: Int,
  amountInCents: Int
) extends StorableJSObject {
  val apexTableName = "STRIPE_REFUNDS"
  val persistenceFields: Map[String, String] = Map(
    "REFUND_ID" -> GetSQLLiteral(refundId),
    "CHARGE_ID" -> GetSQLLiteral(chargeId),
    "CLOSE_ID" -> GetSQLLiteral(closeId),
    "AMOUNT_IN_CENTS" -> GetSQLLiteral(amountInCents)
  )
  val pkColumnName = "REFUND_ID"
  val pkSqlLiteral: String = GetSQLLiteral(refundId)
}