package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import Entities.JsFacades.Stripe.Charge

import scala.concurrent.Future

abstract class StripeAPIIOMechanism {
  def getCharges(since: Option[ZonedDateTime], chargesPerRequest: Int = 100): Future[List[Charge]]
}
