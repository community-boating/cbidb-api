package Reporting.ReportingFilters.ReportingFilterFactories.Person

import Entities.{JpClassInstance, JpClassSession, JpClassType, Person}
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class PersonFilterFactoryMemProgramYear extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
  val argTypes: List[ReportingFilterArgType] = List(ARG_DROPDOWN, ARG_DROPDOWN)
  val displayName: String = "By Class Type"
  val defaultValue: String = "1"
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    val typeId = arg.toInt
    implicit val pb: PersistenceBroker = _pb
    pb.getObjectById(
      Person,
      1
    ).toSet
  })

  def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
    List(
      List(
        ("a", "a")
      ),
      List(
        ("b", "b")
      )
    )
  }
}
