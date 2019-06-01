package Entities.JsFacades.Stripe

import CbiUtil.GetSQLLiteral
import Entities.{CastableToStorableClass, CastableToStorableObject}
import play.api.libs.json.{JsObject, JsString, JsValue, Json}

case class BalanceTransaction(
  id: String,
  amount: Int,
  description: Option[String],
  fee: Int,
  net: Int,
  source: String,
  status: String,
  `type`: String,
  payout: String
) extends CastableToStorableClass {
  val pkSqlLiteral: String = GetSQLLiteral(id)
  val storableObject: CastableToStorableObject[_] = BalanceTransaction
  val persistenceValues: Map[String, String] = BalanceTransaction.persistenceValues(this)
}

object BalanceTransaction extends StripeCastableToStorableObject[BalanceTransaction] {
  implicit val balanceTransactionJSONFormat = Json.format[BalanceTransaction]
  def apply(v: JsValue, po: Payout): BalanceTransaction = {
    val newThing: JsObject = v.as[JsObject] ++ JsObject(Map("payout" -> JsString(po.id)))
    newThing.as[BalanceTransaction]
  }

  def apply(v: JsValue): BalanceTransaction = v.as[BalanceTransaction]

  val apexTableName = "STRIPE_BALANCE_TRANSACTIONS"
  val persistenceFieldsMap: Map[String, BalanceTransaction => String] = Map(
    "TRANSACTION_ID" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.id)),
    "AMOUNT_IN_CENTS" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.amount)),
    "DESCRIPTION" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.description)),
    "FEE_IN_CENTS" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.fee)),
    "NET_IN_CENTS" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.net)),
    "SOURCE" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.source)),
    "STATUS" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.status)),
    "TYPE" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.`type`)),
    "PAYOUT" -> ((bt: BalanceTransaction) => GetSQLLiteral(bt.payout))
  )
  val pkColumnName = "TRANSACTION_ID"
  val getURL: String = "balance/history"
  val getId: BalanceTransaction => String = _.id
}