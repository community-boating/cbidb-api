package org.sailcbi.APIServer.Logic.DockhouseLogic.CreateSignoutLogic

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Util.BitVector
import org.sailcbi.APIServer.Api.Endpoints.Dto.Staff.Dockhouse.CreateSignout.DtoStaffDockhouseCreateSignoutPostRequest
import org.sailcbi.APIServer.Entities.EntityDefinitions.{Signout, SignoutTest}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Logic.IO.DockhouseIo

object CreateSignoutLogic {
	val ERROR_RATING_INSUFFICIENT = CreateSignoutSingleError(code = "rating-insufficient", overridable = true, message = "The skipper does not have the required rating.")
	val ERROR_RATING_UNUSABLE = CreateSignoutSingleError(code = "rating-unusable", overridable = true, message = "The skipper's rating is frozen.")
	val ERROR_SIGNOUT_HOLD = CreateSignoutSingleError(code = "signout-hold", overridable = false, message = "The skipper is currently on signout hold.")
	val ERROR_DISCOUNT_FREEZE = CreateSignoutSingleError(code = "discount-freeze", overridable = false, message = "The skipper's membership is frozen pending discount verification.")
	val ERROR_BAD_REQUEST = CreateSignoutSingleError(code = "bad-request", overridable = false, message = "Invalid signout request.")
	val ERROR_PROGRAM_CLOSED = CreateSignoutSingleError(code = "program-closed", overridable = false, message = "The program is closed.")
	val ERROR_NO_MEMBERSHIP = CreateSignoutSingleError(code = "no-membership", overridable = false, message = "The skipper does not have an active membership.")
	val ERROR_NO_RATING_TRIVIAL = CreateSignoutSingleError(code = "no-rating-trivial", overridable = true, message = "The skipper does not have the required rating; read them the rules and resubmit to grant.")
	val ERROR_INVALID_PURPOSE = CreateSignoutSingleError(code = "invalid-purpose", overridable = false, message = "May not race at the same time as a test/class.")
	val ERROR_UNPASSABLE_TEST = CreateSignoutSingleError(code = "unpassable-test", overridable = true, message = "The skipper's membership does not permit them to hold the rating they are trying to test for.")
	val ERROR_UNPERFORMABLE_TEST = CreateSignoutSingleError(code = "unperformable-test", overridable = true, message = "The weather conditions do not permit this test.")
	val ERROR_INVALID_OCCUPANCY = CreateSignoutSingleError(code = "invalid-occupancy", overridable = false, message = "Invalid occupancy for that boat type.")
	val ERROR_ALREADY_SAILING = CreateSignoutSingleError(code = "already-sailing", overridable = true, message = "At least one person in that boat is already on the water")
	val ERROR_GUESTS_INVALID = CreateSignoutSingleError(code = "guests-invalid", overridable = false, message = "The skipper is not permitted to take guests with them.")
	val ERROR_CREW_INVALID = CreateSignoutSingleError(code = "crew-invalid", overridable = true, message = "Invalid crew.")

	private val errorList = List(
		ERROR_RATING_INSUFFICIENT,
		ERROR_RATING_UNUSABLE,
		ERROR_SIGNOUT_HOLD,
		ERROR_DISCOUNT_FREEZE,
		ERROR_BAD_REQUEST,
		ERROR_PROGRAM_CLOSED,
		ERROR_NO_MEMBERSHIP,
		ERROR_NO_RATING_TRIVIAL,
		ERROR_INVALID_PURPOSE,
		ERROR_UNPASSABLE_TEST,
		ERROR_UNPERFORMABLE_TEST,
		ERROR_INVALID_OCCUPANCY,
		ERROR_ALREADY_SAILING,
		ERROR_GUESTS_INVALID,
		ERROR_CREW_INVALID,
	)

	def attemptSignout(rc: UnlockedRequestCache, req: DtoStaffDockhouseCreateSignoutPostRequest): Either[CreateSignoutError, Signout] = {
		val errors = {
			if (!req.isRacing) {
				List.empty
			} else {
				req.sailNumber match {
					case Some(s)
					=> mapSailNumToArtificalErrors(s)
					case None => List.empty
				}
			}
		}
		errors match {
			case Nil => Right(createSignout(rc, req))
			case l: List[CreateSignoutSingleError] => Left(CreateSignoutError("validation-error", "validation error", l))
		}
	}

	private def createSignout(rc: UnlockedRequestCache, req: DtoStaffDockhouseCreateSignoutPostRequest): Signout = {
		// TODO
		val signoutType = {
			if (req.isRacing) MagicIds.SIGNOUT_TYPES.RACING
			else MagicIds.SIGNOUT_TYPES.SAIL
		}

		val signout = DockhouseIo.createSignout(
			rc = rc,
			personId = req.skipperPersonId,
			programId = req.programId,
			boatId = req.boatId,
			signoutType = signoutType,
			cardNumber = Some(req.skipperCardNumber),
			sailNumber = req.sailNumber,
			hullNumber = req.hullNumber,
			testRatingId = req.skipperTestRatingId,
			isQueued = false,
		)

		val crewObjs = req.signoutCrew.map(crew => {
			DockhouseIo.createSignoutCrew(rc, crew.personId, crew.cardNumber, signout.values.signoutId.get)
		})

		val crewTests = req.signoutCrew.filter(_.testRatingId.isDefined).map(crew => {
			val st = new SignoutTest
			st.values.signoutId.update(signout.values.signoutId.get)
			st.values.personId.update(crew.personId)
			st.values.ratingId.update(crew.testRatingId.get)

			rc.commitObjectToDatabase(st)

			st
		})

		signout.references.crew.set(crewObjs.toIndexedSeq)
		if (crewTests.nonEmpty) {
			// add crew tests to existing skipper test, if present
			val existingTests = signout.references.tests.peek.map(_.toList).getOrElse(List.empty)
			val updatedTests = crewTests ::: existingTests
			signout.references.tests.forceSet(updatedTests.toIndexedSeq)
		}
		signout
	}

	private def mapSailNumToArtificalErrors(sailNumber: String): List[CreateSignoutSingleError] = {
		try {
			val sailNumberInt = sailNumber.toInt
			errorList.zipWithIndex.filter(t => BitVector.testBit(sailNumberInt, t._2)).map(_._1)
		} catch {
			case _: NumberFormatException => List.empty
		}
	}
}
