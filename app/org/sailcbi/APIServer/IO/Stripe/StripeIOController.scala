package org.sailcbi.APIServer.IO.Stripe

import java.time.ZonedDateTime

import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.Entities.CastableToStorableClass
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, StripeError, _}
import org.sailcbi.APIServer.IO.HTTP.{GET, POST}
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.{GetLocalStripeBalanceTransactions, GetLocalStripePayouts}
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import org.sailcbi.APIServer.IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.IO.{COMMIT_TYPE_ASSERT_NO_ACTION, COMMIT_TYPE_DO, COMMIT_TYPE_SKIP, CommitType}
import org.sailcbi.APIServer.Services.Logger.Logger
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StripeIOController(apiIO: StripeAPIIOMechanism, dbIO: StripeDatabaseIOMechanism, logger: Logger)(implicit PA: PermissionsAuthority) {
	def getCharges(since: Option[ZonedDateTime], chargesPerRequest: Int = 100): Future[List[Charge]] =
		apiIO.getStripeList[Charge](
			"charges",
			Charge.apply,
			(c: Charge) => c.id,
			since match {
				case None => List.empty
				case Some(d) => List("created[gte]=" + d.toEpochSecond)
			},
			chargesPerRequest
		).map({
			case Succeeded(t) => t
			case Warning(t, _) => t
			case _ => throw new Exception("failed to get charges")
		})

	def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]] =
		apiIO.getOrPostStripeSingleton("tokens/" + token, Token.apply, GET, None, None)

	def getBalanceTransactions: Future[ServiceRequestResult[List[BalanceTransaction], StripeError]] =
		apiIO.getStripeList(
			"balance/history",
			BalanceTransaction.apply,
			(bt: BalanceTransaction) => bt.id,
			List.empty,
			100
		)

	def createCharge(amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] =
		apiIO.getOrPostStripeSingleton(
			"charges",
			Charge.apply,
			POST,
			Some(Map(
				"amount" -> amountInCents.toString,
				"currency" -> "usd",
				"source" -> token,
				"description" -> ("Charge for orderId " + orderId + " time " + PA.serverParameters.nowDateTimeString),
				"metadata[closeId]" -> closeId.toString,
				"metadata[orderId]" -> orderId.toString,
				"metadata[token]" -> token,
				"metadata[cbiInstance]" -> PA.instanceName
			)),
			Some((c: Charge) => dbIO.createObject(c))
		)

	def syncBalanceTransactions: Future[ServiceRequestResult[(Int, Int, Int), Unit]] = {
		// Update DB with all payouts
		updateLocalDBFromStripeForStorable(
			Payout,
			List.empty,
			None,
			(dbMech: StripeDatabaseIOMechanism) => dbMech.getObjects(Payout, new GetLocalStripePayouts),
			COMMIT_TYPE_DO,
			COMMIT_TYPE_DO,
			COMMIT_TYPE_ASSERT_NO_ACTION
		).flatMap(_ => {
			val payouts: List[Payout] = dbIO.getObjects(Payout, new GetLocalStripePayouts)
			// For each payout, update DB with all associated BTs
			Future.sequence(payouts.map(po => {
				val constructor: (JsValue => BalanceTransaction) = BalanceTransaction.apply(_, po)
				updateLocalDBFromStripeForStorable(
					BalanceTransaction,
					List("payout=" + po.id),
					Some((bt: BalanceTransaction) => bt.`type` != "payout"),
					(dbMech: StripeDatabaseIOMechanism) => dbMech.getObjects(BalanceTransaction, new GetLocalStripeBalanceTransactions(po)),
					COMMIT_TYPE_DO,
					COMMIT_TYPE_DO,
					COMMIT_TYPE_ASSERT_NO_ACTION,
					Some(constructor)
				)
			})).map(serviceResultList => serviceResultList.foldLeft(Succeeded((0, 0, 0)): ServiceRequestResult[(Int, Int, Int), Unit])((result, e) => result match {
				case Succeeded((c, u, d)) => {
					val runningTotals = e.asInstanceOf[Succeeded[(Int, Int, Int), Unit]].successObject
					Succeeded(runningTotals._1 + c, runningTotals._2 + u, runningTotals._3 + d)
				}
				case x => x
			}))
		})
	}

	def updateLocalDBFromStripeForStorable[T <: CastableToStorableClass](
		castableObj: StripeCastableToStorableObject[T],
		getReqParameters: List[String],
		filterGetReqResults: Option[T => Boolean],
		getLocalObjectsQuery: StripeDatabaseIOMechanism => List[T],
		insertCommitType: CommitType,
		updateCommitType: CommitType,
		deleteCommitType: CommitType,
		constructor: Option[JsValue => T] = None
	): Future[ServiceRequestResult[(Int, Int, Int), Unit]] = {
		val localObjects: List[T] = getLocalObjectsQuery(dbIO)
		getRemoteObjects(castableObj, getReqParameters, filterGetReqResults, constructor).map(remotes => {
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
		constructor: Option[(JsValue => T)] = None
	): Future[List[T]] = {
		val defaultConstructor: (JsValue => T) = castableObj.apply
		apiIO.getStripeList(castableObj.getURL, constructor.getOrElse(defaultConstructor), castableObj.getId, getReqParameters, 100).map({
			case s: NetSuccess[List[T], _] => filterGetReqResults match {
				case None => s.successObject
				case Some(f) => s.successObject.filter(f)
			}
			case CriticalError(e) => throw e
			case _ => List.empty
		})
	}

	private def commitDeltaToDatabase[T <: CastableToStorableClass](
		delta: SetDelta[T],
		insertCommitType: CommitType,
		updateCommitType: CommitType,
		deleteCommitType: CommitType
	): ServiceRequestResult[(Int, Int, Int), Unit] = {
		try {
			println("About to commit delta")
			println(delta.toCreate.size + " inserts to do")
			println(delta.toUpdate.size + " updates to do")
			println(delta.toDestroy.size + " deletes to do")
			deleteCommitType match {
				case COMMIT_TYPE_DO => delta.toDestroy.foreach(dbIO.deleteObject(_))
				case COMMIT_TYPE_ASSERT_NO_ACTION => if (delta.toDestroy.nonEmpty) logger.warning("Found unexpected deletes " + delta.toDestroy.map(_.pkSqlLiteral).mkString(","))
				case COMMIT_TYPE_SKIP =>
			}
			updateCommitType match {
				case COMMIT_TYPE_DO => delta.toUpdate.foreach(dbIO.updateObject(_))
				case COMMIT_TYPE_ASSERT_NO_ACTION => if (delta.toUpdate.nonEmpty) logger.warning("Found unexpected updates " + delta.toUpdate.map(_.pkSqlLiteral).mkString(","))
				case COMMIT_TYPE_SKIP =>
			}
			insertCommitType match {
				case COMMIT_TYPE_DO => delta.toCreate.foreach(dbIO.createObject(_))
				case COMMIT_TYPE_ASSERT_NO_ACTION => if (delta.toCreate.nonEmpty) logger.warning("Found unexpected inserts " + delta.toCreate.map(_.pkSqlLiteral).mkString(","))
				case COMMIT_TYPE_SKIP =>
			}
			Succeeded((delta.toCreate.size, delta.toUpdate.size, delta.toDestroy.size))
		} catch {
			case e: Throwable => CriticalError(e)
		}
	}
}
