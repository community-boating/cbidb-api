package Reporting.ReportingFilters.ReportingFilterFactories.Person


import Entities.EntityDefinitions._
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class PersonFilterFactoryRating extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Has Rating"
	val argDefinitions = List(
		(ARG_DROPDOWN, Rating.specialIDs.RATING_ID_MERC_GREEN.toString),
	)

	def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
		implicit val pb: PersistenceBroker = _pb

		type PersonID = Int

		val ratingId: Int = arg.toInt

		val allRatings = pb.getAllObjectsOfClass(Rating)

		val ratingsToHave: List[Int] = Rating.getAllHigherRatingsThanRating(allRatings, ratingId).map(_.values.ratingId.get)

		val personIDs: List[Int] = pb.getObjectsByFilters(
			PersonRating,
			List(PersonRating.fields.ratingId.inList(ratingsToHave)),
			10000
		).map(_.values.personId.get)

		pb.getObjectsByIds(Person, personIDs, 10000).toSet
	})

	// TODO: exclude inactive?  Filter them to the bottom?
	def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
		val ratings: List[Rating] = pb.getAllObjectsOfClass(Rating)
		List(ratings.sortWith((a, b) => a.values.ratingName.get < b.values.ratingName.get).map(r =>
			(r.values.ratingId.get.toString, r.values.ratingName.get.toString)
		))
	}
}
