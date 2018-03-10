package IO.Stripe

import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

import CbiUtil.{GenerateSetDelta, SetDelta}
import Entities.JsFacades.Stripe.Charge
import IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StripeIOController(apiIO: StripeAPIIOMechanism, dbIO: StripeDatabaseIOMechanism) {
  def updateLocalChargesFromAPIForClose(closeId: Int, closeOpenDateTime: ZonedDateTime, closeFinalizedDateTime: Option[ZonedDateTime]): Unit = {
    val delta = getAPItoDBChargeDelta(closeId, closeOpenDateTime, closeFinalizedDateTime)
    commitChargeDeltaToDatabase(delta)
  }

  def getAPItoDBChargeDelta(closeId: Int, closeOpenDateTime: ZonedDateTime, closeFinalizedDateTime: Option[ZonedDateTime]): SetDelta[Charge] = {
    val filterChargesToClose: Charge => Boolean =
      c => c.metadata.closeId.getOrElse("") == closeId.toString
    val getChargeID: Charge => String = c => c.id
    val currentLocalCharges: Set[Charge] = dbIO.getLocalChargesForClose(closeId).toSet

    // Unfortunately we can't ask stripe to just give us all charges with a given close ID.
    // So, first try getting all charges created up to 24 hours before the close was opened.
    // If for some reason the database has charges that aren't in that result set from Stripe,
    // try one more time, going back two months before the close opened, just to be super sure
    val oneDayGracePeriod: Set[Charge] = Await.result(
      apiIO.getCharges(Some(closeOpenDateTime.minusDays(1))),
      Duration.create(10, TimeUnit.SECONDS)
    ).filter(filterChargesToClose).toSet

    val oneDayDelta = GenerateSetDelta(oneDayGracePeriod, currentLocalCharges, getChargeID)
    if (oneDayDelta.toDestroy.isEmpty) {
      oneDayDelta
    } else {
      val twoMonthGracePeriod: Set[Charge] = Await.result(
        apiIO.getCharges(Some(closeOpenDateTime.minusMonths(2))),
        Duration.create(50, TimeUnit.SECONDS)
      ).filter(filterChargesToClose).toSet
      GenerateSetDelta(twoMonthGracePeriod, currentLocalCharges, getChargeID)
    }
  }

  private def commitChargeDeltaToDatabase(delta: SetDelta[Charge]): Unit = {
    delta.toCreate.foreach(dbIO.createCharge)
    delta.toUpdate.foreach(dbIO.updateCharge)
    delta.toDestroy.foreach(dbIO.deleteCharge)
  }
}
