package IO.Stripe

import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

import CbiUtil.{GenerateSetDelta, ServiceRequestResult, SetDelta}
import Entities.JsFacades.Stripe.{Charge, StripeError, Token}
import IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

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
    // So, go back two months before the close opened, just to be super sure
    val twoMonthGracePeriod: Set[Charge] = Await.result(
      apiIO.getCharges(Some(closeOpenDateTime.minusMonths(2))),
      Duration.create(50, TimeUnit.SECONDS)
    ).filter(filterChargesToClose).toSet
    GenerateSetDelta(twoMonthGracePeriod, currentLocalCharges, getChargeID)
  }

  def createCharge(amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] = {
    apiIO.createCharge(dbIO, amountInCents, token, orderId, closeId)
  }

  def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]] =
    apiIO.getTokenDetails(token)

  private def commitChargeDeltaToDatabase(delta: SetDelta[Charge]): Unit = {
    delta.toCreate.foreach(dbIO.createCharge)
    delta.toUpdate.foreach(dbIO.updateCharge)
    delta.toDestroy.foreach(dbIO.deleteCharge)
  }
}
