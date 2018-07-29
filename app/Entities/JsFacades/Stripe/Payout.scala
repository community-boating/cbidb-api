package Entities.JsFacades.Stripe

import java.time.{Instant, ZonedDateTime}

import CbiUtil.{DateUtil, GetSQLLiteral}
import Entities.{CastableToStorableClass, CastableToStorableObject}
import play.api.libs.json.{JsValue, Json}

case class Payout(
  id: String,
  amount: Int,
  arrivalDate: Long,
  balanceTransactionId: String,
  status: String
) extends CastableToStorableClass {
  val pkSqlLiteral: String = GetSQLLiteral(id)
  val storableObject: CastableToStorableObject[_] = Payout
  val persistenceValues: Map[String, String] = Payout.persistenceValues(this)

  val arrivalZonedDateTime: ZonedDateTime =
    ZonedDateTime.ofInstant(Instant.ofEpochSecond(arrivalDate), DateUtil.HOME_TIME_ZONE)
}

object Payout extends StripeCastableToStorableObject[Payout] {
  implicit val balanceTransactionJSONFormat = Json.format[Payout]
  def apply(v: JsValue): Payout = {
    println(v.toString())
    v.as[Payout]
  }

  val apexTableName = "STRIPE_PAYOUTS"
  val persistenceFieldsMap: Map[String, Payout => String] = Map(
    "PAYOUT_ID" -> ((p: Payout) => GetSQLLiteral(p.id)),
    "AMOUNT_IN_CENTS" -> ((p: Payout) => GetSQLLiteral(p.amount)),
    "ARRIVAL_DATETIME" -> ((p: Payout) => GetSQLLiteral(p.arrivalZonedDateTime)),
    "BALANCE_TRANSACTION_ID" -> ((p: Payout) => GetSQLLiteral(p.balanceTransactionId)),
    "STATUS" -> ((p: Payout) => GetSQLLiteral(p.status))
  )
  val pkColumnName = "PAYOUT_ID"
  val getURL: String = "payouts"
}