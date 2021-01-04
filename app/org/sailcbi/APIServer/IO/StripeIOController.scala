package org.sailcbi.APIServer.IO

import org.sailcbi.APIServer.CbiUtil._
import org.sailcbi.APIServer.Entities.CastableToStorableClass
import org.sailcbi.APIServer.Entities.JsFacades.Stripe.{Charge, StripeError, _}
import org.sailcbi.APIServer.IO.HTTP.{GET, POST}
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.{GetLocalStripeBalanceTransactions, GetLocalStripeCharges, GetLocalStripePayouts}
import org.sailcbi.APIServer.IO.PreparedQueries.{PreparedQueryForSelect, PreparedQueryForUpdateOrDelete}
import org.sailcbi.APIServer.Services.Authentication.{ApexUserType, MemberUserType, PublicUserType, UserType}
import org.sailcbi.APIServer.Services.Exception.UnauthorizedAccessException
import org.sailcbi.APIServer.Services.Logger.Logger
import org.sailcbi.APIServer.Services.StripeAPIIO.StripeAPIIOMechanism
import org.sailcbi.APIServer.Services.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.Services.{PermissionsAuthority, PersistenceBroker, RequestCache, ResultSetWrapper}
import play.api.libs.json.{JsObject, JsString, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StripeIOController[T <: UserType](rc: RequestCache[T], apiIO: StripeAPIIOMechanism, dbIO: StripeDatabaseIOMechanism, logger: Logger)(implicit PA: PermissionsAuthority) {
	def getTokenDetails(token: String): Future[ServiceRequestResult[Token, StripeError]] = {
		if (TestUserType(Set(ApexUserType, PublicUserType, MemberUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton("tokens/" + token, Token.apply, GET, None, None)
		}
		else throw new UnauthorizedAccessException("getTokenDetails denied to userType " + rc.auth.companion)
	}

	def createCharge(amountInCents: Int, source: String, orderId: Number, closeId: Number): Future[ServiceRequestResult[Charge, StripeError]] = {
		if (TestUserType(Set(ApexUserType, MemberUserType, PublicUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				"charges",
				Charge.apply,
				POST,
				Some(Map(
					"amount" -> amountInCents.toString,
					"currency" -> "usd",
					"source" -> source,
					"description" -> ("Charge for orderId " + orderId + " time " + PA.serverParameters.nowDateTimeString),
					"metadata[closeId]" -> closeId.toString,
					"metadata[orderId]" -> orderId.toString,
					"metadata[source]" -> source,
					"metadata[cbiInstance]" -> PA.instanceName
				)),
				Some((c: Charge) => dbIO.createObject(c))
			)
		}
		else throw new UnauthorizedAccessException("createCharge denied to userType " + rc.auth.companion)
	}

	/** If you call this, you must put the PI ID on the order */
	def createPaymentIntent(orderId: Int, source: Option[String], totalInCents: Int, customerId: String, closeId: Int): Future[ServiceRequestResult[PaymentIntent, StripeError]] = {
		if (TestUserType(Set(MemberUserType, ApexUserType), rc.auth.companion)) {
			val sourceBody = source match {
				case None => Map.empty
				case Some(s) => Map("payment_method" -> s)
			}
			apiIO.getOrPostStripeSingleton(
				"payment_intents",
				PaymentIntent.apply,
				POST,
				Some(Map(
					"amount" -> totalInCents.toString,
					"currency" -> "usd",
					"customer" -> customerId,
					"capture_method" -> "automatic",
					"description" -> ("Charge for orderId " + orderId),
					"metadata[orderId]" -> orderId.toString,
					"metadata[closeId]" -> closeId.toString,
					"metadata[cbiInstance]" -> PA.instanceName
				) ++ sourceBody),
				None
			)
		}
		else throw new UnauthorizedAccessException("createPaymentIntent denied to userType " + rc.auth.companion)
	}

	def updatePaymentIntentWithTotal(intentId: String, totalInCents: Int, closeId: Int): Future[ServiceRequestResult[Unit, StripeError]] = {
		if (TestUserType(Set(MemberUserType, ApexUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				"payment_intents/" + intentId,
				_ => Unit,
				POST,
				Some(Map(
					"amount" -> totalInCents.toString,
					"metadata[closeId]" -> closeId.toString,
				)),
				None
			)
		}
		else throw new UnauthorizedAccessException("updatePaymentIntentWithTotal denied to userType " + rc.auth.companion)
	}

	def updatePaymentIntentWithPaymentMethod(intentId: String, methodId: String): Future[ServiceRequestResult[PaymentIntent, StripeError]] = {
		if (TestUserType(Set(MemberUserType, ApexUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				"payment_intents/" + intentId,
				PaymentIntent.apply,
				POST,
				Some(Map(
					"payment_method" -> methodId,
				)),
				None
			)
		}
		else throw new UnauthorizedAccessException("updatePaymentIntentWithPaymentMethod denied to userType " + rc.auth.companion)
	}

	def getPaymentIntent(paymentIntentId: String): Future[ServiceRequestResult[PaymentIntent, StripeError]] = {
		if (TestUserType(Set(MemberUserType, ApexUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				"payment_intents/" + paymentIntentId,
				PaymentIntent.apply,
				GET,
				None,
				None
			)
		}
		else throw new UnauthorizedAccessException("getPaymentIntent denied to userType " + rc.auth.companion)
	}

	def confirmPaymentIntent(paymentIntentId: String): Future[ServiceRequestResult[PaymentIntent, StripeError]] = {
		if (TestUserType(Set(MemberUserType, ApexUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				"payment_intents/" + paymentIntentId + "/confirm",
				PaymentIntent.apply,
				POST,
				Some(Map.empty),
				Some((pi: PaymentIntent) => pi.charges.data.filter(c => c.paid).map(dbIO.createObject))
			)
		}
		else throw new UnauthorizedAccessException("confirmPaymentIntent denied to userType " + rc.auth.companion)
	}

	def detachPaymentMethod(paymentMethodId: String): Future[ServiceRequestResult[Unit, StripeError]] = {
		if (TestUserType(Set( MemberUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				s"payment_methods/${paymentMethodId}/detach",
				null,
				POST,
				Some(Map.empty),
				None
			)
		}
		else throw new UnauthorizedAccessException("detachPaymentMethod denied to userType " + rc.auth.companion)
	}

	def syncBalanceTransactions: Future[ServiceRequestResult[(Int, Int, Int), Unit]] = {
		if (TestUserType(Set(ApexUserType), rc.auth.companion)) {
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
						Thread.sleep(400)
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
		else throw new UnauthorizedAccessException("syncBalanceTransactions denied to userType " + rc.auth.companion)
	}

	def createStripeCustomerFromPerson(rc: RequestCache[_], personId: Int): Future[ServiceRequestResult[Customer, StripeError]] = {
		val (optionEmail, optionStripeCustomerId) = {
			type ValidationResult = (Option[String], Option[String]) // email, existing stripe customerID
			val q = new PreparedQueryForSelect[ValidationResult](Set(MemberUserType)) {
				override def mapResultSetRowToCaseObject(rsw: ResultSetWrapper): ValidationResult =
					(rsw.getOptionString(1), rsw.getOptionString(2))

				override val params: List[String] = List(personId.toString)

				override def getQuery: String =
					"""
					  |select email, stripe_customer_id from persons where person_id = ?
					  |""".stripMargin
			}
			rc.executePreparedQueryForSelect(q).head
		}

		if (optionEmail.isEmpty){
			println("stripe create customer: email is blank")
			Future(ValidationError(StripeError("validation", "Email address cannot be blank.")))
		} else if (optionStripeCustomerId.nonEmpty) {
			println("stripe create customer: exists")
			Future(ValidationError(StripeError("validation", "Stripe customer record already exists.")))
		} else {
			if (TestUserType(Set(MemberUserType), rc.auth.asInstanceOf[UserType].companion)) {
				apiIO.getOrPostStripeSingleton(
					"customers",
					Customer.apply,
					POST,
					Some(Map(
						"email" -> optionEmail.get,
						"metadata[personId]" -> personId.toString,
						"metadata[cbiInstance]" -> PA.instanceName
					)),
					Some((c: Customer) => {
						val update = new PreparedQueryForUpdateOrDelete(Set(MemberUserType)) {
							override val params: List[String] = List(c.id, personId.toString)
							override def getQuery: String =
								"""
								  |update persons set stripe_customer_id = ? where person_id = ?
								  |""".stripMargin
						}
						rc.executePreparedQueryForUpdateOrDelete(update)
					})
				)
			}
			else throw new UnauthorizedAccessException("createStripeCustomerFromPerson denied to userType " + rc.auth.asInstanceOf[UserType].companion)
		}
	}

	def getCustomerPortalURL(customerId: String): Future[ServiceRequestResult[String, StripeError]] = {
		if (TestUserType(Set(MemberUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				"billing_portal/sessions",
				(jsv: JsValue) => jsv.asInstanceOf[JsObject]("url").asInstanceOf[JsString].value,
				POST,
				Some(Map(
					"customer" -> customerId
				)),
				None
			)
		}
		else throw new UnauthorizedAccessException("getCustomerPortalURL denied to userType " + rc.auth.companion)
	}

	def storePaymentMethod(customerId: String, paymentMethodId: String): Future[ServiceRequestResult[_, StripeError]] = {
		if (TestUserType(Set(MemberUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton(
				s"payment_methods/$paymentMethodId/attach",
				PaymentMethod.apply,
				POST,
				Some(Map(
					"customer" -> customerId
				)),
				None
			).flatMap({
				case f: NetFailure[PaymentMethod, StripeError] => Future(f.asInstanceOf[ServiceRequestResult[PaymentMethod, StripeError]])
				case _: NetSuccess[PaymentMethod, StripeError] =>
					apiIO.getOrPostStripeSingleton(
						"customers/" + customerId,
						Customer.apply,
						POST,
						Some(Map(
							"invoice_settings[default_payment_method]" -> paymentMethodId
						)),
						None
					)
			})
		}
		else throw new UnauthorizedAccessException("storePaymentMethod denied to userType " + rc.auth.companion)
	}

	def getCustomerObject(customerId: String): Future[ServiceRequestResult[Customer, StripeError]] = {
		if (TestUserType(Set(MemberUserType, ApexUserType), rc.auth.companion)) {
			apiIO.getOrPostStripeSingleton("customers/" + customerId, Customer.apply, GET, None, None)
		}
		else throw new UnauthorizedAccessException("getCustomerObject denied to userType " + rc.auth.companion)
	}

	def getCustomerDefaultPaymentMethod(customerId: String): Future[ServiceRequestResult[Option[PaymentMethod], StripeError]] = {
		getCustomerObject(customerId).flatMap({
			case f: NetFailure[_, StripeError] => Future(f.asInstanceOf[ServiceRequestResult[Option[PaymentMethod], StripeError]])
			case c: NetSuccess[Customer, StripeError] => c.successObject.invoice_settings.default_payment_method match {
				case None => Future(Succeeded(None))
				case Some(paymentMethod) => apiIO.getOrPostStripeSingleton(
					PaymentMethod.getURL + "/" + paymentMethod,
					jsv => Some(PaymentMethod.apply(jsv)),
					GET,
					None,
					None
				)
			}
		})
	}

	def chargeCustomersDefaultPaymentMethod(
		customerId: String,
		amountInCents: Int,
		paymentStaggerId: Int,
		orderId: Int,
		closeId: Int
	): Future[ServiceRequestResult[Charge, StripeError]] = {
		if (TestUserType(Set(ApexUserType, MemberUserType), rc.auth.companion)) {
			getCustomerObject(customerId).flatMap({
				case f: NetFailure[_, StripeError] => Future(f.asInstanceOf[ServiceRequestResult[Charge, StripeError]])
				case c: NetSuccess[Customer, StripeError] => c.successObject.invoice_settings.default_payment_method match {
					case None => throw new Exception("No default payment method for customer " + customerId)
					case Some(paymentMethod) => apiIO.getOrPostStripeSingleton(
						"charges",
						Charge.apply,
						POST,
						Some(Map(
							"amount" -> amountInCents.toString,
							"customer" -> customerId,
							"currency" -> "usd",
							"source" -> paymentMethod,
							"description" -> ("Charge for paymentStaggerId " + paymentStaggerId + " time " + PA.serverParameters.nowDateTimeString),
							"metadata[closeId]" -> closeId.toString,
							"metadata[orderId]" -> orderId.toString,
							"metadata[paymentStaggerId]" -> paymentStaggerId.toString,
							"metadata[cbiInstance]" -> PA.instanceName
						)),
						Some((c: Charge) => dbIO.createObject(c))
					)
				}
			})
		}
		else throw new UnauthorizedAccessException("createCharge denied to userType " + rc.auth.companion)
	}

	private def updateLocalDBFromStripeForStorable[T_Stor <: CastableToStorableClass](
		castableObj: StripeCastableToStorableObject[T_Stor],
		getReqParameters: List[String],
		filterGetReqResults: Option[T_Stor => Boolean],
		getLocalObjectsQuery: StripeDatabaseIOMechanism => List[T_Stor],
		insertCommitType: CommitType,
		updateCommitType: CommitType,
		deleteCommitType: CommitType,
		constructor: Option[JsValue => T_Stor] = None
	): Future[ServiceRequestResult[(Int, Int, Int), Unit]] = {
		val localObjects: List[T_Stor] = getLocalObjectsQuery(dbIO)
		getRemoteObjects(castableObj, getReqParameters, filterGetReqResults, constructor).map(remotes => {
			commitDeltaToDatabase(
				GenerateSetDelta(remotes.toSet, localObjects.toSet, castableObj.getId),
				insertCommitType,
				updateCommitType,
				deleteCommitType
			)
		})
	}

	private def getRemoteObjects[T_Stor <: CastableToStorableClass](
		castableObj: StripeCastableToStorableObject[T_Stor],
		getReqParameters: List[String],
		filterGetReqResults: Option[T_Stor => Boolean],
		constructor: Option[(JsValue => T_Stor)] = None
	): Future[List[T_Stor]] = {
		val defaultConstructor: (JsValue => T_Stor) = castableObj.apply
		apiIO.getStripeList(castableObj.getURL, constructor.getOrElse(defaultConstructor), castableObj.getId, getReqParameters, 100).map({
			case s: NetSuccess[List[T_Stor], _] => filterGetReqResults match {
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
