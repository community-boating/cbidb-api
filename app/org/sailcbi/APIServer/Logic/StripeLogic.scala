package org.sailcbi.APIServer.Logic

import org.sailcbi.APIServer.Entities.JsFacades.Stripe.Charge

object StripeLogic {
	def groupChargesByCloseID(charges: List[Charge]): Map[Option[Int], List[Charge]] =
		charges.groupBy(_.metadata.closeId.map(_.toInt))
}
