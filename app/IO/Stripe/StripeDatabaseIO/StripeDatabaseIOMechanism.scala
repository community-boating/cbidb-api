package IO.Stripe.StripeDatabaseIO

import Stripe.JsFacades.Charge

abstract class StripeDatabaseIOMechanism {
  def createCharge(c: Charge): Unit
  def updateCharge(c: Charge): Unit
  def deleteCharge(chargeID: String): Unit
  def getLocalCharges: List[Charge]
}
