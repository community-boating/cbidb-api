package Entities.JsFacades.Stripe

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZonedDateTime}

import CbiUtil.{DateUtil, GetSQLLiteral}
import Entities.{CastableToStorableClass, CastableToStorableObject}
import play.api.libs.json.{JsValue, Json}

case class Payout(
  id: String,
  amount: Int,
  arrival_date: Long,
  balance_transaction: String,
  status: String
) extends CastableToStorableClass {
  val pkSqlLiteral: String = GetSQLLiteral(id)
  val storableObject: CastableToStorableObject[_] = Payout
  val persistenceValues: Map[String, String] = Payout.persistenceValues(this)

  lazy val arrivalZonedDateTime: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(arrival_date), DateUtil.HOME_TIME_ZONE)
}

object Payout extends StripeCastableToStorableObject[Payout] {
  implicit val balanceTransactionJSONFormat = Json.format[Payout]
  def apply(v: JsValue): Payout = v.as[Payout]

  val apexTableName = "STRIPE_PAYOUTS"
  val persistenceFieldsMap: Map[String, Payout => String] = Map(
    "PAYOUT_ID" -> ((p: Payout) => GetSQLLiteral(p.id)),
    "AMOUNT_IN_CENTS" -> ((p: Payout) => GetSQLLiteral(p.amount)),
    "ARRIVAL_DATETIME" -> ((p: Payout) => GetSQLLiteral(p.arrivalZonedDateTime)),
    "BALANCE_TRANSACTION_ID" -> ((p: Payout) => GetSQLLiteral(p.balance_transaction)),
    "STATUS" -> ((p: Payout) => GetSQLLiteral(p.status))
  )
  val pkColumnName = "PAYOUT_ID"
  val getURL: String = "payouts"
  val getId: Payout => String = _.id
}