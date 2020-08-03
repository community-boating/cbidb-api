package org.sailcbi.APIServer.IO

import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.Entities.CastableToStorableClass
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, StripeError, _}
import org.sailcbi.APIServer.IO.HTTP.{GET, POST}
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.{GetLocalStripeBalanceTransactions, GetLocalStripeCharges, GetLocalStripePayouts}
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, MemberUserType, PublicUserType}
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.Logger.Logger
import org.sailcbi.APIServer.Services.StripeAPIIO.StripeAPIIOMechanism
import org.sailcbi.APIServer.Services.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.Services.{PermissionsAuthority, RequestCache}
import play.api.libs.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StripeIOController(rc: RequestCache, apiIO: StripeAPIIOMechanism, dbIO: StripeDatabaseIOMechanism, logger: Logger)(implicit PA: PermissionsAuthority) {
	def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]] = {
		if (TestUserType(Set(ApexUserType, PublicUserType, MemberUserType), rc.auth.userType)) {
			apiIO.getOrPostStripeSingleton("tokens/" + token, Token.apply, GET, None, None)
		}
		else throw new UnauthorizedAccessException("getCharges denied to userType " + rc.auth.userType)
	}

	def createCharge(amountInCents: Int, token: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] = {
		if (TestUserType(Set(ApexUserType, MemberUserType, PublicUserType), rc.auth.userType)) {
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
		}
		else throw new UnauthorizedAccessException("getCharges denied to userType " + rc.auth.userType)
	}

	def syncBalanceTransactions: Future[ServiceRequestResult[(Int, Int, Int), Unit]] = {
		if (TestUserType(Set(ApexUserType), rc.auth.userType)) {
			// Update DB with all payouts
			updateLocalDBFromStripeForStorable(
				Charge,
				List.empty,
				Some((c: Charge) => c.status == "succeeded"),
				(dbMech: StripeDatabaseIOMechanism) => dbMech.getObjects(Charge, new GetLocalStripeCharges),
				COMMIT_TYPE_DO,
				COMMIT_TYPE_DO,
				COMMIT_TYPE_ASSERT_NO_ACTION
			).flatMap(_ =>
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
			)
		}
		else throw new UnauthorizedAccessException("getCharges denied to userType " + rc.auth.userType)
	}

	private def updateLocalDBFromStripeForStorable[T <: CastableToStorableClass](
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

	private def getRemoteObjects[T <: CastableToStorableClass](
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
