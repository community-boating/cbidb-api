package IO.Stripe.StripeDatabaseIO

import Entities.{CastableToStorableClass, CastableToStorableObject}
import Entities.JsFacades.Stripe.{BalanceTransaction, Charge, ChargeRefund, Payout}
import IO.PreparedQueries.HardcodedQueryForSelect
import Services.PersistenceBroker

class StripeDatabaseIOMechanism(pb: PersistenceBroker) {
  val whitelistedClasses: Set[CastableToStorableObject[_]] = Set(
    Charge,
    ChargeRefund,
    BalanceTransaction,
    Payout
  )

  def createObject[T <: CastableToStorableClass](t: T): Unit = {
    if (whitelistedClasses contains t.storableObject) {
      println("creating!")
      pb.executePreparedQueryForInsert(t.getInsertPreparedQuery)
    } else throw new Exception("Stripe DB adapter got an uncool create request")
  }

  def updateObject[T <: CastableToStorableClass](t: T): Unit = {
    if (whitelistedClasses contains t.storableObject) {
      pb.executePreparedQueryForUpdateOrDelete(t.getUpdatePreparedQuery)
    } else throw new Exception("Stripe DB adapter got an uncool update request")
  }

  def deleteObject[T <: CastableToStorableClass](t: T): Unit = {
    if (whitelistedClasses contains t.storableObject) {
      pb.executePreparedQueryForUpdateOrDelete(t.getDeletePreparedQuery)
    } else throw new Exception("Stripe DB adapter got an uncool delete request")
  }

  def getObjects[T <: CastableToStorableClass](obj: CastableToStorableObject[T], pq: HardcodedQueryForSelect[T]): List[T] = {
    if (whitelistedClasses contains obj) {
      pb.executePreparedQueryForSelect(pq)
    } else List.empty
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
