package org.sailcbi.APIServer.Entities.JsFacades.Stripe

import com.coleji.framework.Core.RequestCacheObject
import com.coleji.framework.IO.PreparedQueries.PreparedValue
import com.coleji.framework.Storable.{CastableToStorableClass, CastableToStorableObject}
import com.coleji.framework.Util.DateUtil
import org.sailcbi.APIServer.UserTypes.ApexRequestCache
import play.api.libs.json.{JsValue, Json}

import java.time.{Instant, ZonedDateTime}

case class Payout(
						 id: String,
						 amount: Int,
						 arrival_date: Long,
						 balance_transaction: String,
						 status: String
				 ) extends CastableToStorableClass {
	val pkSqlLiteral: String = id
	val storableObject: CastableToStorableObject[_] = Payout
	val persistenceValues: Map[String, PreparedValue] = Payout.persistenceValues(this)

	lazy val arrivalZonedDateTime: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(arrival_date), DateUtil.HOME_TIME_ZONE)
}

object Payout extends StripeCastableToStorableObject[Payout] {
	implicit val balanceTransactionJSONFormat = Json.format[Payout]

	override val allowedUserTypes: Set[RequestCacheObject[_]] = Set(ApexRequestCache)

	def apply(v: JsValue): Payout = v.as[Payout]

	val apexTableName = "STRIPE_PAYOUTS"
	val persistenceFieldsMap: Map[String, Payout => PreparedValue] = Map(
		"PAYOUT_ID" -> ((p: Payout) => p.id),
		"AMOUNT_IN_CENTS" -> ((p: Payout) => p.amount),
		"ARRIVAL_DATETIME" -> ((p: Payout) => p.arrivalZonedDateTime),
		"BALANCE_TRANSACTION_ID" -> ((p: Payout) => p.balance_transaction),
		"STATUS" -> ((p: Payout) => p.status)
	)
	val pkColumnName = "PAYOUT_ID"
	val getURL: String = "payouts"
	val getId: Payout => String = _.id
}