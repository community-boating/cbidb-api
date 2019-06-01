package IO.Stripe

import java.time.ZonedDateTime

import CbiUtil._
import Entities.CastableToStorableClass
import Entities.JsFacades.Stripe.{Charge, StripeError, _}
import IO.HTTP.{GET, POST}
import IO.PreparedQueries.Apex.{GetLocalStripeBalanceTransactions, GetLocalStripePayouts}
import IO.Stripe.StripeAPIIO.StripeAPIIOMechanism
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import IO.{COMMIT_TYPE_ASSERT_NO_ACTION, COMMIT_TYPE_DO, COMMIT_TYPE_SKIP, CommitType}
import Services.Logger.Logger
import Services.{PermissionsAuthority, ServerStateContainer}
import play.api.libs.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StripeIOController(apiIO: StripeAPIIOMechanism, dbIO: StripeDatabaseIOMechanism, logger: Logger) {
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
				"description" -> ("Charge for orderId " + orderId + " time " + ServerStateContainer.get.nowDateTimeString),
				"metadata[closeId]" -> closeId.toString,
				"metadata[orderId]" -> orderId.toString,
				"metadata[token]" -> token,
				"metadata[cbiInstance]" -> PermissionsAuthority.instanceName.get
			)),
			Some((c: Charge) => dbIO.createObject(c))
		)

	def syncBalanceTransactions: Future[ServiceRequestResult[Unit, Unit]] = {
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
			})).map(serviceResultList => serviceResultList.foldLeft(Succeeded(Unit): ServiceRequestResult[Unit, Unit])((result, e) => result match {
				case Succeeded(_) => e
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
																		): Future[ServiceRequestResult[Unit, Unit]] = {
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
																   ): ServiceRequestResult[Unit, Unit] = {
		try {
			println("About to commit delta")
			println(delta.toCreate.size + " inserts to do")
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
			Succeeded(Unit)
		} catch {
			case e: Throwable => CriticalError(e)
		}
	}
}
