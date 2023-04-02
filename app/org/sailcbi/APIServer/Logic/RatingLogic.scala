package org.sailcbi.APIServer.Logic

import com.coleji.neptune.Util.ListUtil
import org.sailcbi.APIServer.Entities.EntityDefinitions.{BoatType, PersonRating, ProgramType, Rating}
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Entities.entitycalculations.MaxBoatFlag
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

	def maxFlags(
		boatTypes: List[BoatType],
		programs: List[ProgramType],
		prs: List[PersonRating],
		rs: List[Rating]
	): List[MaxBoatFlag] = {
		val intermediate = boatTypes.map(b =>
			(b, programs.map(p =>
				(p, maxFlagForBoat(prs, rs, b.values.boatId.get, p.values.programId.get))
			))
		)
		intermediate.flatMap(b => b._2.filter(_._2.nonEmpty).map(p => MaxBoatFlag(
			boatId = b._1.values.boatId.get,
			programId = p._1.values.programId.get,
			maxFlag = p._2.map(t => t._1).get
		)))
	}

	/**
	 * MUST pass Ratings list from the ratings cache (with BRs populated)
	 */
	def maxFlagForBoat(prs: List[PersonRating], rs: List[Rating], boatId: Int, programId: Int): Option[(String, PersonRating)] = {
		ListUtil.findAndReturn(FLAG_ORDER)(f => canSailBoatProgram(prs, rs, boatId, programId, f).map(pr => (f, pr)))
	}

	/**
	 *
	 * @param prs Always pass in ALL prs for a given person
	 * @param rs Always pass in ALL ratings from the ratings cache (with BRs populated)
	 * @param programId Desired program Id
	 * @param flag Desired flag
	 * @return
	 */
	def canSailBoatProgram(prs: List[PersonRating], rs: List[Rating], boatId: Int, programId: Int, flag: String): Option[PersonRating] = {
		val ratingHash = rs.map(r => (r.values.ratingId.get, r)).toMap
		val brs = rs.flatMap(_.references.boats.get)

		val neededRatings = brs.filter(br =>
			br.values.programId.get == programId &&
			br.values.boatId.get == boatId &&
			br.values.flag.get == flag
		)

		neededRatings
			.map(_.values.ratingId.get)
			.map(hasRatingForProgram(prs, ratingHash, programId))
			.filter(_.isDefined)
			.map(_.get)
			.headOption
	}

	def hasRatingForProgram(prs: List[PersonRating], ratingHash: Map[Int, Rating], programId: Int)(ratingId: Int): Option[PersonRating] = {
		val matches = prs.filter(pr =>
			pr.values.programId.get == programId &&
			pr.values.ratingId.get == ratingId
		)
		if (matches.nonEmpty) Some(matches.head)
		else {
			val rating = ratingHash(ratingId)
			rating.values.overriddenBy.get match {
				case None => None
				case Some(r) => hasRatingForProgram(prs, ratingHash, programId)(r)
			}
		}
	}
}
