package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.Person


import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export._
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Logic.RatingLogic

class PersonFilterFactoryRating extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Has Rating"
	val argDefinitions = List(
		(ARG_DROPDOWN, MagicIds.RATING_IDS.MERCURY_GREEN.toString),
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		implicit val rc: UnlockedRequestCache = _rc

		type PersonID = Int

		val ratingId: Int = arg.toInt

		val allRatings = rc.getAllObjectsOfClass(Rating, Set(
			Rating.fields.ratingId,
			Rating.fields.ratingName,
			Rating.fields.overriddenBy,
		))

		val ratingsToHave: List[Int] = RatingLogic.getAllHigherRatingsThanRating(allRatings, ratingId).map(_.values.ratingId.get)

		val personIDs: List[Int] = rc.getObjectsByFilters(
			PersonRating,
			List(PersonRating.fields.ratingId.alias.inList(ratingsToHave)),
			Set(PersonRating.primaryKey),
			10000
		).map(_.values.personId.get)

		rc.getObjectsByIds(Person, personIDs, Set(Person.primaryKey), 10000).toSet
	})

	// TODO: exclude inactive?  Filter them to the bottom?
	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val ratings: List[Rating] = rc.getAllObjectsOfClass(Rating, Set(
			Rating.fields.ratingId,
			Rating.fields.ratingName,
			Rating.fields.overriddenBy,
		))
		List(ratings.sortWith((a, b) => a.values.ratingName.get < b.values.ratingName.get).map(r =>
			(r.values.ratingId.get.toString, r.values.ratingName.get.toString)
		))
	}
}
