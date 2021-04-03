package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.Person


import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.RequestCache

class PersonFilterFactoryRating extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Has Rating"
	val argDefinitions = List(
		(ARG_DROPDOWN, Rating.specialIDs.RATING_ID_MERC_GREEN.toString),
	)

	def getFilter(rc: RequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: RequestCache) => {
		implicit val rc: RequestCache = _rc

		type PersonID = Int

		val ratingId: Int = arg.toInt

		val allRatings = rc.getAllObjectsOfClass(Rating)

		val ratingsToHave: List[Int] = Rating.getAllHigherRatingsThanRating(allRatings, ratingId).map(_.values.ratingId.get)

		val personIDs: List[Int] = rc.getObjectsByFilters(
			PersonRating,
			List(PersonRating.fields.ratingId.inList(ratingsToHave)),
			10000
		).map(_.values.personId.get)

		rc.getObjectsByIds(Person, personIDs, 10000).toSet
	})

	// TODO: exclude inactive?  Filter them to the bottom?
	def getDropdownValues(rc: RequestCache): List[List[(String, String)]] = {
		val ratings: List[Rating] = rc.getAllObjectsOfClass(Rating)
		List(ratings.sortWith((a, b) => a.values.ratingName.get < b.values.ratingName.get).map(r =>
			(r.values.ratingId.get.toString, r.values.ratingName.get.toString)
		))
	}
}