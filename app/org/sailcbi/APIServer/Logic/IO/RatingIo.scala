package org.sailcbi.APIServer.Logic.IO

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.{PersonRating, RatingChange}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

import java.time.ZonedDateTime

object RatingIo {
	val RATING_ACTION_GRANT = "G"
	val RATING_ACTION_RESCIND = "R"

	private val findRating = (personId: Int, programId: Int, ratingId: Int) => (pr: PersonRating) => {
		pr.values.personId.get == personId &&
			pr.values.programId.get == programId &&
			pr.values.ratingId.get == ratingId
	}

	def doGrant(rc: UnlockedRequestCache, existingRatings: List[PersonRating], personId: Int, ratingId: Int, programId: Int, instructor: String): Unit = {
		val existingRating = existingRatings.find(findRating(personId, programId, ratingId))

		val newRating = existingRating match {
			case Some(_) => None
			case None => {
				val pr = new PersonRating
				pr.values.personId.update(personId)
				pr.values.programId.update(programId)
				pr.values.ratingId.update(ratingId)

				rc.commitObjectToDatabase(pr)
				Some(pr)
			}
		}

		val newRatingList = newRating match {
			case Some(pr) => pr :: existingRatings
			case None => existingRatings
		}

		// Unconditionally add the change record
		val change: RatingChange = new RatingChange
		change.values.personId.update(personId)
		change.values.programId.update(programId)
		change.values.ratingId.update(ratingId)
		change.values.action.update(RATING_ACTION_GRANT)
		change.values.changedBy.update(Some(instructor))
		change.values.changedDate.update(rc.PA.now())
		rc.commitObjectToDatabase(change)

		// In AP, rigging + verbal automatically also adds merc green
		if (
			programId == MagicIds.PROGRAM_TYPES.ADULT_PROGRAM_ID &&
				(ratingId == MagicIds.RATING_IDS.RIGGING || ratingId == MagicIds.RATING_IDS.VERBAL)
		) {
			val findRigging = findRating(personId, programId, MagicIds.RATING_IDS.RIGGING)
			val findVerbal = findRating(personId, programId, MagicIds.RATING_IDS.VERBAL)

			if (newRatingList.exists(findRigging) && newRatingList.exists(findVerbal)) {
				doGrant(rc, newRatingList, personId, MagicIds.RATING_IDS.MERCURY_GREEN, programId, instructor)
			}
		}
	}

	def doRescind(rc: StaffRequestCache, pr: PersonRating, instructor: String, now: ZonedDateTime, comments: Option[String]): Unit = {
		rc.deleteObjectsById(PersonRating, List(pr.values.assignId.get))

		val change = new RatingChange
		change.values.personId.update(pr.values.personId.get)
		change.values.programId.update(pr.values.programId.get)
		change.values.ratingId.update(pr.values.ratingId.get)
		change.values.action.update(RATING_ACTION_RESCIND)
		change.values.changedBy.update(Some(instructor))
		change.values.changedDate.update(now.toLocalDateTime)
		change.values.comments.update(comments)

		rc.commitObjectToDatabase(change)
	}
}
