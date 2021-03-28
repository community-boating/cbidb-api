package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import com.coleji.framework.Storable.{CastableToStorableClass, CastableToStorableObject}
import org.sailcbi.APIServer.CbiUtil.GetSQLLiteral
import org.sailcbi.APIServer.IO.PreparedQueries.PreparedValue
import org.sailcbi.APIServer.Services.Authentication.ApexRequestCache
import org.sailcbi.APIServer.Services.RequestCacheObject
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
	payout: String,
	created: Int
) extends CastableToStorableClass {
	val pkSqlLiteral: String = GetSQLLiteral(id)
	val storableObject: CastableToStorableObject[_] = BalanceTransaction
	val persistenceValues: Map[String, PreparedValue] = BalanceTransaction.persistenceValues(this)
}

object BalanceTransaction extends StripeCastableToStorableObject[BalanceTransaction] {
	implicit val balanceTransactionJSONFormat = Json.format[BalanceTransaction]

	override val allowedUserTypes: Set[RequestCacheObject[_]] = Set(ApexRequestCache)

	def apply(v: JsValue, po: Payout): BalanceTransaction = {
		val newThing: JsObject = v.as[JsObject] ++ JsObject(Map("payout" -> JsString(po.id)))
		newThing.as[BalanceTransaction]
	}

	def apply(v: JsValue): BalanceTransaction = v.as[BalanceTransaction]

	val apexTableName = "STRIPE_BALANCE_TRANSACTIONS"
	val persistenceFieldsMap: Map[String, BalanceTransaction => PreparedValue] = Map(
		"TRANSACTION_ID" -> ((bt: BalanceTransaction) => bt.id),
		"AMOUNT_IN_CENTS" -> ((bt: BalanceTransaction) => bt.amount),
		"DESCRIPTION" -> ((bt: BalanceTransaction) => bt.description),
		"FEE_IN_CENTS" -> ((bt: BalanceTransaction) => bt.fee),
		"NET_IN_CENTS" -> ((bt: BalanceTransaction) => bt.net),
		"SOURCE" -> ((bt: BalanceTransaction) => bt.source),
		"STATUS" -> ((bt: BalanceTransaction) => bt.status),
		"TYPE" -> ((bt: BalanceTransaction) => bt.`type`),
		"PAYOUT" -> ((bt: BalanceTransaction) => bt.payout),
		"CREATED" -> ((bt: BalanceTransaction) => bt.created)
	)
	val pkColumnName = "TRANSACTION_ID"
	val getURL: String = "balance/history"
	val getId: BalanceTransaction => String = _.id
}