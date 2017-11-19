package Reporting.ReportingFilters.ReportingFilterFactories.Person

import Entities.{Person, ProgramType}
import Reporting.ReportingFilters._
import Services.PersistenceBroker

// First arg is program
// second arg is year
class PersonFilterFactoryMemProgramYear extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
  val argTypes: List[ReportingFilterArgType] = List(ARG_DROPDOWN, ARG_INT)
  val displayName: String = "By Class Type"
  val defaultValue: String = "1"
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb

    val twoArgs = arg.split(",")
    val programId = twoArgs(0).toInt
    val year = twoArgs(1).toInt

    pb.getObjectById(Person, 1).toSet
  })

  def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
    val programs: List[ProgramType] = pb.getAllObjectsOfClass(ProgramType)
    List(programs.map(p =>
      (p.values.programId.toString, p.values.programName.toString)
    ))
  }
}
