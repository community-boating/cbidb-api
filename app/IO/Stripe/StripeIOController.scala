package IO.Stripe

import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

import CbiUtil._
import Entities.{CastableToStorableClass, CastableToStorableObject}
import Entities.JsFacades.Stripe.{Charge, StripeCastableToStorableObject, StripeError, Token}
import IO.{COMMIT_TYPE_ASSERT_NO_ACTION, COMMIT_TYPE_DO, COMMIT_TYPE_SKIP, CommitType}
import IO.PreparedQueries.HardcodedQueryForSelect
import IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import Services.Logger.Logger
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class StripeIOController(apiIO: StripeAPIIOMechanism, dbIO: StripeDatabaseIOMechanism, logger: Logger) {
  def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]] =
    apiIO.getTokenDetails(token)

  def createCharge(amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] = {
    apiIO.createCharge(dbIO, amountInCents, token, orderId, closeId)
  }

  def updateLocalDBFromStripeForStorable[T <: CastableToStorableClass](
    castableObj: StripeCastableToStorableObject[T],
    getReqParameters: List[String],
    filterGetReqResults: Option[T => Boolean],
    getLocalObjectsQuery: StripeDatabaseIOMechanism => List[T],
    insertCommitType: CommitType,
    updateCommitType: CommitType,
    deleteCommitType: CommitType
  ): Unit = {
    val localObjects: List[T] = getLocalObjectsQuery(dbIO)
    getRemoteObjects(castableObj, getReqParameters, filterGetReqResults).map(remotes => {
      commitDeltaToDatabase(
        GenerateSetDelta(remotes.toSet, localObjects.toSet, castableObj.getId),
        insertCommitType,
        updateCommitType,
        deleteCommitType
      )
    })

  }

  def getRemoteObjects[T <: CastableToStorableClass](
    castableObj: StripeCastableToStorableObject[T],
    getReqParameters: List[String],
    filterGetReqResults: Option[T => Boolean],
  ): Future[List[T]] = apiIO.getStripeList(castableObj.getURL, castableObj.apply, castableObj.getId, getReqParameters, 100).map({
    case s: NetSuccess[List[T], _] => s.successObject
    case _ => List.empty
  })

  private def commitDeltaToDatabase[T <: CastableToStorableClass](
    delta: SetDelta[T],
    insertCommitType: CommitType,
    updateCommitType: CommitType,
    deleteCommitType: CommitType
  ): Unit = {
    insertCommitType match {
      case COMMIT_TYPE_DO => delta.toCreate.foreach(dbIO.createObject(_))
      case COMMIT_TYPE_ASSERT_NO_ACTION => if (delta.toCreate.nonEmpty) logger.warning("Found unexpected inserts " + delta.toCreate.map(_.pkSqlLiteral).mkString(","))
      case COMMIT_TYPE_SKIP =>
    }
    updateCommitType match {
      case COMMIT_TYPE_DO => delta.toUpdate.foreach(dbIO.updateObject(_))
      case COMMIT_TYPE_ASSERT_NO_ACTION => if (delta.toUpdate.nonEmpty) logger.warning("Found unexpected updates " + delta.toUpdate.map(_.pkSqlLiteral).mkString(","))
      case COMMIT_TYPE_SKIP =>
    }
    deleteCommitType match {
      case COMMIT_TYPE_DO => delta.toDestroy.foreach(dbIO.deleteObject(_))
      case COMMIT_TYPE_ASSERT_NO_ACTION => if (delta.toDestroy.nonEmpty) logger.warning("Found unexpected deletes " + delta.toDestroy.map(_.pkSqlLiteral).mkString(","))
      case COMMIT_TYPE_SKIP =>
    }
  }

  /*
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



  private def commitChargeDeltaToDatabase(delta: SetDelta[Charge]): Unit = {
    delta.toCreate.foreach(dbIO.createCharge)
    delta.toUpdate.foreach(dbIO.updateCharge)
    delta.toDestroy.foreach(dbIO.deleteCharge)
  }*/
}
