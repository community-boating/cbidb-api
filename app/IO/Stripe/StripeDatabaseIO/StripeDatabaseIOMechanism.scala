package IO.Stripe.StripeDatabaseIO

import Entities.JsFacades.Stripe.Charge
import IO.PreparedQueries.Apex.GetLocalStripeChargesForClose
import Services.PersistenceBroker

class StripeDatabaseIOMechanism(pb: PersistenceBroker) {
  def createCharge(c: Charge): Unit = pb.executePreparedQueryForInsert(c.getInsertPreparedQuery)
  def updateCharge(c: Charge): Unit = pb.executePreparedQueryForUpdateOrDelete(c.getUpdatePreparedQuery)
  def deleteCharge(c: Charge): Unit = pb.executePreparedQueryForUpdateOrDelete(c.getDeletePreparedQuery)
  def getLocalChargesForClose(closeId: Int): List[Charge] = pb.executePreparedQueryForSelect(new GetLocalStripeChargesForClose(closeId))
}
