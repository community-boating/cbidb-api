package IO.Stripe.StripeDatabaseIO

import Entities.JsFacades.Stripe.{BalanceTransaction, Charge, ChargeRefund, Payout}
import Entities.{CastableToStorableClass, CastableToStorableObject}
import IO.PreparedQueries.HardcodedQueryForSelect
import Services.PersistenceBroker

class StripeDatabaseIOMechanism(pb: PersistenceBroker) {
	val self = this
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


}
