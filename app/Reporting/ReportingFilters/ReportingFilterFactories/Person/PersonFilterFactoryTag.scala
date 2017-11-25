package Reporting.ReportingFilters.ReportingFilterFactories.Person

import Entities._
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class PersonFilterFactoryTag extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
  val argTypes: List[ReportingFilterArgType] = List(ARG_DROPDOWN)
  val displayName: String = "Has Tag"
  val defaultValue: String = "4"
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb

    type PersonID = Int

    val tagId: Int = arg.toInt

    val allTags = pb.getAllObjectsOfClass(Tag)

    val personIDs: List[Int] = pb.getObjectsByFilters(
      PersonTag,
      List(PersonTag.fields.tagId.equalsConstant(tagId)),
      10000
    ).map(_.values.personId.get)

    pb.getObjectsByIds(Person, personIDs, 10000).toSet
  })

  // TODO: exclude inactive?  Filter them to the bottom?
  def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
    val allTags = pb.getAllObjectsOfClass(Tag)
    List(allTags.sortWith((a, b) => a.values.tagName.get < b.values.tagName.get).map(r =>
      (r.values.tagId.get.toString, r.values.tagName.get.toString)
    ))
  }
}
