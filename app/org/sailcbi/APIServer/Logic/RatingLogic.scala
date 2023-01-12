package org.sailcbi.APIServer.Logic

import org.sailcbi.APIServer.UserTypes.StaffRequestCache

object RatingLogic {
	def userCanGrantRating(rc: StaffRequestCache, ratingId: Int, programId: Int): Boolean = true
}
