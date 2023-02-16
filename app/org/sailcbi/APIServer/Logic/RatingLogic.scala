package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Util.ListUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{BoatRating, PersonRating, Rating, RatingProgram}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.UserTypes.StaffRequestCache

object RatingLogic {
	val FLAG_ORDER = List(
		MagicIds.FLAG_COLORS.FLAG_RED,
		MagicIds.FLAG_COLORS.FLAG_YELLOW,
		MagicIds.FLAG_COLORS.FLAG_GREEN,
	)

	type RatingCheckResult = RatingCheckResult.Value
	object RatingCheckResult extends Enumeration {
		val OK, NO_RATING, FROZEN, WRONG_PROGRAM = Value
	}

	def userCanGrantRating(rc: StaffRequestCache, ratingId: Int, programId: Int): Boolean = true

	/**
	 * MUST pass Ratings list from the ratings cache (with BRs populated)
	 */
	def maxFlag(prs: List[PersonRating], rs: List[Rating], boatId: Int, programId: Int): Option[(String, PersonRating)] = {
		ListUtil.findAndReturn(FLAG_ORDER)(f => ratingCheck(prs, rs, boatId, programId, f).map(pr => (f, pr)))
	}

	/**
	 *
	 * @param prs Always pass in ALL prs for a given person
	 * @param rs Always pass in ALL ratings from the ratings cache (with BRs populated)
	 * @param programId Desired program Id
	 * @param flag Desired flag
	 * @return
	 */
	def ratingCheck(prs: List[PersonRating], rs: List[Rating], boatId: Int, programId: Int, flag: String): Option[PersonRating] = {
		val ratingHash = rs.map(r => (r.values.ratingId.get, r)).toMap
		val brs = rs.flatMap(_.references.boats.get)

		val neededRatings = brs.filter(br =>
			br.values.programId.get == programId &&
			br.values.boatId.get == boatId &&
			br.values.flag.get == flag
		)

		def findRating(ratingId: Int): Option[PersonRating] = {
			val matches = prs.filter(pr =>
				pr.values.programId.get == programId &&
				pr.values.ratingId.get == ratingId
			)
			if (matches.nonEmpty) Some(matches.head)
			else {
				val rating = ratingHash(ratingId)
				rating.values.overriddenBy.get match {
					case None => None
					case Some(r) => findRating(r)
				}
			}
		}

		neededRatings
			.map(_.values.ratingId.get)
			.map(findRating)
			.filter(_.isDefined)
			.map(_.get)
			.headOption
	}
}
