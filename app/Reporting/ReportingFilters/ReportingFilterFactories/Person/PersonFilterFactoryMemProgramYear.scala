package Reporting.ReportingFilters.ReportingFilterFactories.Person

import Entities.Entities._
import Reporting.ReportingFilters._
import Services.PersistenceBroker
import Services.ServerBootLoader.ssc

// First arg is program
// second arg is year
class PersonFilterFactoryMemProgramYear extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
  val displayName: String = "Had mem in prog X, year Y"
  val argDefinitions = List(
    (ARG_DROPDOWN, ProgramType.specialIDs.PROGRAM_TYPE_ID_AP.toString),
    (ARG_INT, ssc.currentSeason().toString)
  )
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb

    type PersonID = Int

    val twoArgs = arg.split(",")
    val programId = twoArgs(0).toInt
    val year = twoArgs(1).toInt
    println("programId is " + programId + " and year is " + year)

    val membershipTypeIDs = pb.getObjectsByFilters(
      MembershipType,
      List(MembershipType.fields.programId.equalsConstant(programId)),
      15
    ).map(_.values.membershipTypeId.get)

    val expiredInYear: Set[PersonID] = pb.getObjectsByFilters(
      PersonMembership,
      List(
        PersonMembership.fields.membershipTypeId.inList(membershipTypeIDs),
        PersonMembership.fields.expirationDate.isYearConstant(year)
      ),
      500
    ).map(_.values.personId.get).toSet

    val startedInYear: Set[PersonID] = pb.getObjectsByFilters(
      PersonMembership,
      List(
        PersonMembership.fields.membershipTypeId.inList(membershipTypeIDs),
        PersonMembership.fields.startDate.isYearConstant(year)
      ),
      500
    ).map(_.values.personId.get).toSet

    val personIDs: Set[PersonID] = expiredInYear union startedInYear

    pb.getObjectsByIds(
      Person,
      personIDs.toList,
      1000
    ).toSet
  })

  def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
    val programs: List[ProgramType] = pb.getAllObjectsOfClass(ProgramType)
    List(programs.map(p =>
      (p.values.programId.get.toString, p.values.programName.get.toString)
    ))
  }
}
