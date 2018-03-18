package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import CbiUtil.ServiceRequestResult
import Entities.JsFacades.Stripe.{Charge, StripeError, Token}
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism

import scala.concurrent.Future

abstract class StripeAPIIOMechanism {
  def getCharges(since: Option[ZonedDateTime], chargesPerRequest: Int = 100): Future[List[Charge]]

  def createCharge(dbIO: StripeDatabaseIOMechanism, amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]]

  def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]]
}
