package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import CbiUtil.ServiceRequestResult
import Entities.JsFacades.Stripe.{BalanceTransaction, Charge, StripeError, Token}
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import play.api.libs.json.JsValue

import scala.concurrent.Future

abstract class StripeAPIIOMechanism {
  //def getCharges(since: Option[ZonedDateTime], chargesPerRequest: Int = 100): Future[List[Charge]]

  def createCharge(dbIO: StripeDatabaseIOMechanism, amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]]

  def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]]

  def getBalanceTransactions: Future[ServiceRequestResult[List[BalanceTransaction], StripeError]]

  def getStripeList[T](
    url: String,
    constructor: JsValue => T,
    getID: T => String,
    params: List[String],
    fetchSize: Int
  ): Future[ServiceRequestResult[List[T], StripeError]]
}
