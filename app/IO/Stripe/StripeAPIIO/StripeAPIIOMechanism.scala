package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import Stripe.JsFacades.Charge

import scala.concurrent.Future

abstract class StripeAPIIOMechanism {
  def getCharges(since: Option[ZonedDateTime]): Future[List[Charge]]
}
