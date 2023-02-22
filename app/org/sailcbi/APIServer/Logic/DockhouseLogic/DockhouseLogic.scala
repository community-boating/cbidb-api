package org.sailcbi.APIServer.Logic.DockhouseLogic

import com.coleji.neptune.Core.UnlockedRequestCache
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassSignup, FlagChange, PersonRating}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Logic.IO.{DockhouseIo, RatingIo}

object DockhouseLogic {
	def putFlagChange(rc: UnlockedRequestCache, flagColor: String): Either[String, FlagChange] = {
		val ok = flagColor match {
			case MagicIds.FLAG_COLORS.FLAG_GREEN => true
			case MagicIds.FLAG_COLORS.FLAG_YELLOW => true
			case MagicIds.FLAG_COLORS.FLAG_RED => true
			case _ => false
		}

		if (ok) {
			Right(DockhouseIo.putFlagChange(rc, flagColor))
		} else {
			Left("Unrecognized flag color " + flagColor)
		}
	}

	def addPersonToApClass(rc: UnlockedRequestCache, personId: Int, instanceId: Int): Either[String, ApClassSignup] = {
		// TODO: for DH, confirm the instance has a session today
		DockhouseIo.addPersonToApClass(rc, personId, instanceId)
	}

	// TODO: check for access to grant, check if people are members etc
	def grantRatings(rc: UnlockedRequestCache, instructor: String, programId: Int, ratingIds: List[Int], personIds: List[Int]): Unit = {
		personIds.foreach(personId => {
			val prs = rc.getObjectsByFilters(PersonRating, List(PersonRating.fields.personId.alias.equalsConstant(personId)), Set(
				PersonRating.fields.assignId,
				PersonRating.fields.personId,
				PersonRating.fields.ratingId,
				PersonRating.fields.programId
			))

			ratingIds.foreach(ratingId => RatingIo.doGrant(rc, prs, personId, ratingId, programId, instructor))
		})
	}
}
