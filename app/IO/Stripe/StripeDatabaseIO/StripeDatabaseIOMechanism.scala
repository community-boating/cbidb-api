package IO.Stripe.StripeDatabaseIO

import Entities.{CastableToStorableClass, CastableToStorableObject}
import Entities.JsFacades.Stripe.{BalanceTransaction, Charge, ChargeRefund}
import Services.PersistenceBroker

class StripeDatabaseIOMechanism(pb: PersistenceBroker) {
  val whitelistedClasses: Set[CastableToStorableObject[_]] = Set(
    Charge,
    ChargeRefund,
    BalanceTransaction
  )

  def createObject[T <: CastableToStorableClass](t: T): Unit = {
    if (whitelistedClasses contains t.storableObject) {
      pb.executePreparedQueryForInsert(t.getInsertPreparedQuery)
    }
  }

  def updateObject[T <: CastableToStorableClass](t: T): Unit = {
    if (whitelistedClasses contains t.storableObject) {
      pb.executePreparedQueryForUpdateOrDelete(t.getUpdatePreparedQuery)
    }
  }

  def deleteObject[T <: CastableToStorableClass](t: T): Unit = {
    if (whitelistedClasses contains t.storableObject) {
      pb.executePreparedQueryForUpdateOrDelete(t.getDeletePreparedQuery)
    }
  }

  /*

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

  def createBalanceTransaction(bt: BalanceTransaction): Unit =
    pb.executePreparedQueryForInsert(bt.getInsertPreparedQuery)
    */
}
