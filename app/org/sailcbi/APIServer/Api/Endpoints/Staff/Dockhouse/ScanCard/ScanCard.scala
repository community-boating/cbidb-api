package org.sailcbi.APIServer.Api.Endpoints.Staff.Dockhouse.ScanCard

import com.coleji.neptune.API.ResultError
import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, UnlockedRequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.PersonMembership.GetPersonMembership
import org.sailcbi.APIServer.Entities.EntityDefinitions.{BoatType, Person, PersonCard, PersonRating, ProgramType, Rating}
import org.sailcbi.APIServer.Entities.cacheable.{BoatTypes, Programs, Ratings}
import org.sailcbi.APIServer.Logic.RatingLogic
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ScanCard @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(cardNumber: String)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(StaffRequestCache)(None, parsedRequest, rc => {
			scanCard(rc, cardNumber)
				.fold(
					f => Future(Ok(f.asJsObject)),
					s => Future(Ok(s.toJson)),
				)
		})
	})

	private def scanCard(rc: UnlockedRequestCache, cardNumber: String): Either[ResultError, CardScanResult] = {
		for(
			_ <- validateCardNumber(cardNumber);
			pc <- lookupCard(rc, cardNumber);
			ret <- constructResult(rc, pc)
		) yield ret
	}

	/**
	 * return CardScanResult.ERROR_NOT_FOUND if the number is not exactly 7 characters long
	 */
	private def validateCardNumber(cardNumber: String): Either[ResultError, Unit] = {
		if (cardNumber == null || cardNumber.length != 7) Left(CardScanResult.ERROR_NOT_FOUND)
		else Right()
	}

	private def lookupCard(rc: UnlockedRequestCache, cardNumber: String): Either[ResultError, PersonCard] = {
		val result = rc.getObjectsByFilters(
			PersonCard,
			List(PersonCard.fields.cardNum.alias.equalsConstantLowercase(cardNumber)),
			Set(PersonCard.fields.personId, PersonCard.fields.cardNum, PersonCard.fields.active)
		)

		if (result.size != 1) Left(CardScanResult.ERROR_NOT_FOUND)
		else {
			val pc = result.head
			if (!pc.values.active.get) Left(CardScanResult.ERROR_INACTIVE)
			else Right(pc)
		}
	}

	private def constructMemberships(rc: UnlockedRequestCache, personId: Int): List[CardScanResultMembership] = {
		GetPersonMembership.getAllForPerson(rc, personId)
		.filter(_.isActive(LocalDate.now))
		.map(pm => CardScanResultMembership(
			assignId = pm.values.assignId.get,
			membershipTypeId = pm.values.membershipTypeId.get,
			startDate = pm.values.startDate.get,
			expirationDate = pm.values.expirationDate.get,
			discountName = pm.references.discountInstance.get.map(_.references.discount.get.values.discountName.get),
			isDiscountFrozen = false, // TODO
			hasGuestPrivs = pm.references.guestPriv.get.nonEmpty
		))
	}

	private def constructRatings(rc: UnlockedRequestCache, personId: Int): List[CardScanResultRating] = {
		val ratingsQb = QueryBuilder
			.from(PersonRating)
			.innerJoin(Rating, Rating.fields.ratingId.alias.equalsField(PersonRating.fields.ratingId.alias))
			.where(PersonRating.fields.personId.alias.equalsConstant(personId))
			.select(List(
				PersonRating.fields.ratingId,
				PersonRating.fields.programId,
				Rating.fields.ratingName
			))

		rc.executeQueryBuilder(ratingsQb).map(qbrr => {
			val pr = PersonRating.construct(qbrr)

			CardScanResultRating(
				ratingId = pr.values.ratingId.get,
				programId = pr.values.programId.get,
				ratingName = Rating.construct(qbrr).values.ratingName.get,
				status = "Y" // TODO
			)
		})
	}

	private def constructResult(rc: UnlockedRequestCache, pc: PersonCard): Either[ResultError, CardScanResult] = {
		val person = rc.getObjectById(Person, pc.values.personId.get, Set(
			Person.fields.personId,
			Person.fields.nameFirst,
			Person.fields.nameLast,
			Person.fields.specialNeeds,
			Person.fields.memberComment,
			Person.fields.signoutBlockReason
		)).get

		val personId = person.values.personId.get

		val boatTypes = BoatTypes.get(rc, null)
		val ratings = Ratings.get(rc, null)
		val programs = Programs.get(rc, null)
		val prsQb = QueryBuilder
			.from(PersonRating)
			.where(PersonRating.fields.personId.alias.equalsConstant(personId))
			.select(List(
				PersonRating.fields.assignId,
				PersonRating.fields.personId,
				PersonRating.fields.programId,
				PersonRating.fields.ratingId
			))

		val prs = rc.executeQueryBuilder(prsQb).map(PersonRating.construct)

		val maxFlags: List[MaxRatingsResult] =
			RatingLogic.maxFlags(boatTypes._1.toList, programs._1.toList, prs, ratings._1.toList)
				.flatMap(b => b._2.map(p => MaxRatingsResult(b._1, p._1, p._2.map(t => t._1))))
				.filter(_.maxFlag.nonEmpty)

		Right(CardScanResult(
			cardNumber = pc.values.cardNum.get,
			personId = pc.values.personId.get,
			nameFirst = person.values.nameFirst.get.getOrElse(""),
			nameLast = person.values.nameLast.get.getOrElse(""),
			bannerComment = person.values.memberComment.get,
			signoutBlockReason = person.values.signoutBlockReason.get,
			specialNeeds = person.values.specialNeeds.get,
			activeMemberships = constructMemberships(rc, personId),
			personRatings = constructRatings(rc, personId),
			maxFlagsPerBoat = maxFlags
		))
	}

	case class CardScanResultMembership(
		assignId: Int,
		membershipTypeId: Int,
		startDate: Option[LocalDate],
		expirationDate: Option[LocalDate],
		discountName: Option[String],
		isDiscountFrozen: Boolean,
		hasGuestPrivs: Boolean
	)

	case class CardScanResultRating(
		ratingId: Int,
		programId: Int,
		ratingName: String,
		status: String
	)

	case class CardScanResult(
		cardNumber: String,
		personId: Int,
		nameFirst: String,
		nameLast: String,
		bannerComment: Option[String],
		signoutBlockReason: Option[String],
		specialNeeds: Option[String],
		activeMemberships: List[CardScanResultMembership],
		personRatings: List[CardScanResultRating],
		maxFlagsPerBoat: List[MaxRatingsResult]
	) {
		def toJson: JsValue = {
			implicit val successFormat = CardScanResult.writes
			Json.toJson(this)
		}
	}

	case class MaxRatingsResult (
		$$boatType: BoatType,
		$$programType: ProgramType,
		maxFlag: Option[String]
	)

	object CardScanResult {
		val ERROR_NOT_FOUND = ResultError(code = "card-not-found", message = "Card not found.")
		val ERROR_INACTIVE = ResultError(code = "card-inactive", message = "Card inactive.")

		implicit val membershipWrites = Json.writes[CardScanResultMembership]
		implicit val ratingWrites = Json.writes[CardScanResultRating]
		implicit val boatWrites = BoatType.storableJsonWrites
		implicit val programWrites = ProgramType.storableJsonWrites
		implicit val maxRatingWrites = Json.writes[MaxRatingsResult]
		implicit val writes = Json.writes[CardScanResult]

	//	def apply(v: JsValue): CardScanResult = v.as[CardScanResult]
	}
}