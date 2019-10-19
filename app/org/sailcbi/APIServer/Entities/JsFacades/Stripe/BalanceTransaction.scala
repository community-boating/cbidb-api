package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import org.sailcbi.APIServer.CbiUtil.{GetSQLLiteral, GetSQLLiteralPrepared}
import org.sailcbi.APIServer.Entities.{CastableToStorableClass, CastableToStorableObject}
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, UserType}
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

	override val allowedUserTypes: Set[UserType] = Set(ApexUserType)

	def apply(v: JsValue, po: Payout): BalanceTransaction = {
		val newThing: JsObject = v.as[JsObject] ++ JsObject(Map("payout" -> JsString(po.id)))
		newThing.as[BalanceTransaction]
	}

	def apply(v: JsValue): BalanceTransaction = v.as[BalanceTransaction]

	val apexTableName = "STRIPE_BALANCE_TRANSACTIONS"
	val persistenceFieldsMap: Map[String, BalanceTransaction => String] = Map(
		"TRANSACTION_ID" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.id)),
		"AMOUNT_IN_CENTS" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.amount)),
		"DESCRIPTION" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.description)),
		"FEE_IN_CENTS" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.fee)),
		"NET_IN_CENTS" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.net)),
		"SOURCE" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.source)),
		"STATUS" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.status)),
		"TYPE" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.`type`)),
		"PAYOUT" -> ((bt: BalanceTransaction) => GetSQLLiteralPrepared(bt.payout))
	)
	val pkColumnName = "TRANSACTION_ID"
	val getURL: String = "balance/history"
	val getId: BalanceTransaction => String = _.id
}