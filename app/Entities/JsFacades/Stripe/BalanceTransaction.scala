package Entities.JsFacades.Stripe

import CbiUtil.GetSQLLiteral
import Entities.StorableJSObject
import play.api.libs.json.{JsValue, Json}

case class BalanceTransaction(
  id: String,
  amount: Int,
  description: String,
  fee: Int,
  net: Int,
  source: String,
  status: String,
  `type`: String
) extends StorableJSObject {
  val self = this
  val apexTableName = "STRIPE_BALANCE_TRANSACTIONS"
  val persistenceFields: Map[String, String] = Map(
    "TRANSACTION_ID" -> GetSQLLiteral(id),
    "AMOUNT_IN_CENTS" -> GetSQLLiteral(amount),
    "DESCRIPTION" -> GetSQLLiteral(description),
    "FEE_IN_CENTS" -> GetSQLLiteral(fee),
    "NET_IN_CENTS" -> GetSQLLiteral(net),
    "SOURCE" -> GetSQLLiteral(source),
    "STATUS" -> GetSQLLiteral(status),
    "TYPE" -> GetSQLLiteral(`type`),
  )
  val pkColumnName = "TRANSACTION_ID"
  val pkSqlLiteral: String = GetSQLLiteral(id)
}

object BalanceTransaction {
  implicit val balanceTransactionJSONFormat = Json.format[BalanceTransaction]
  def apply(v: JsValue): BalanceTransaction = v.as[BalanceTransaction]
}