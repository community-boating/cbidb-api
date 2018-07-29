package IO.Stripe.StripeDatabaseIO

import Entities.JsFacades.Stripe.{BalanceTransaction, Charge}
import IO.PreparedQueries.Apex.GetLocalStripeChargesForClose
import Services.PersistenceBroker

class StripeDatabaseIOMechanism(pb: PersistenceBroker) {
  def createCharge(c: Charge): Unit = {
    pb.executePreparedQueryForInsert(c.getInsertPreparedQuery)
    c.refunds.foreach(r => pb.executePreparedQueryForInsert(r.getInsertPreparedQuery))
  }
  def updateCharge(c: Charge): Unit = {
    pb.executePreparedQueryForUpdateOrDelete(c.getUpdatePreparedQuery)
    c.refunds.foreach(r => {
      pb.executePreparedQueryForUpdateOrDelete(r.getDeletePreparedQuery)
      pb.executePreparedQueryForInsert(r.getInsertPreparedQuery)
    })
  }
  def deleteCharge(c: Charge): Unit = {
    c.refunds.foreach(r => pb.executePreparedQueryForUpdateOrDelete(r.getDeletePreparedQuery))
    pb.executePreparedQueryForUpdateOrDelete(c.getDeletePreparedQuery)
  }
  def getLocalChargesForClose(closeId: Int): List[Charge] = pb.executePreparedQueryForSelect(new GetLocalStripeChargesForClose(closeId))
  def createBalanceTransaction(bt: BalanceTransaction): Unit =
    pb.executePreparedQueryForInsert(bt.getInsertPreparedQuery)
}
