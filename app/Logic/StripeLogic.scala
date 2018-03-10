package Logic

import Stripe.JsFacades.Charge

object StripeLogic {
  def groupChargesByCloseID(charges: List[Charge]): Map[Option[Int], List[Charge]] =
    charges.groupBy(_.metadata.closeId.map(_.toInt))
}
