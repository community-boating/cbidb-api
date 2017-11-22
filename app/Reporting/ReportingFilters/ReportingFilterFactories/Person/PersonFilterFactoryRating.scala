package Reporting.ReportingFilters.ReportingFilterFactories.Person

import Entities._
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class PersonFilterFactoryRating extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
  val argTypes: List[ReportingFilterArgType] = List(ARG_DROPDOWN)
  val displayName: String = "Has Rating"
  val defaultValue: String = "261"
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb

    type PersonID = Int

    val ratingId: Int = arg.toInt


    // TODO: This is wrong, this is directs only
    val personIDs: List[Int] = pb.getObjectsByFilters(
      PersonRating,
      List(PersonRating.fields.ratingId.equalsConstant(ratingId))
    ).map(_.values.personId.get)

    pb.getObjectsByIds(Person, personIDs, 1000).toSet
  })

  // TODO: exclude inactive?  Filter them to the bottom?
  def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
    val ratings: List[Rating] = pb.getAllObjectsOfClass(Rating)
    List(ratings.sortWith((a, b) => a.values.ratingName.get < b.values.ratingName.get).map(r =>
      (r.values.ratingId.get.toString, r.values.ratingName.get.toString)
    ))
  }
}
