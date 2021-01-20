package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.Person

import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.RequestCache

// First arg is program
// second arg is year
class PersonFilterFactoryMemProgramYear extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Had mem in prog X, year Y"
	val argDefinitions = List(
		(ARG_DROPDOWN, ProgramType.specialIDs.PROGRAM_TYPE_ID_AP.toString),
		(ARG_INT, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: RequestCache[_], arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: RequestCache[_]) => {
		implicit val rc: RequestCache[_] = _rc

		type PersonID = Int

		val twoArgs = arg.split(",")
		val programId = twoArgs(0).toInt
		val year = twoArgs(1).toInt
		println("programId is " + programId + " and year is " + year)

		val membershipTypeIDs = rc.getObjectsByFilters(
			MembershipType,
			List(MembershipType.fields.programId.equalsConstant(programId)),
			15
		).map(_.values.membershipTypeId.get)

		val expiredInYear: Set[PersonID] = rc.getObjectsByFilters(
			PersonMembership,
			List(
				PersonMembership.fields.membershipTypeId.inList(membershipTypeIDs),
				PersonMembership.fields.expirationDate.isYearConstant(year)
			),
			500
		).map(_.values.personId.get).toSet

		val startedInYear: Set[PersonID] = rc.getObjectsByFilters(
			PersonMembership,
			List(
				PersonMembership.fields.membershipTypeId.inList(membershipTypeIDs),
				PersonMembership.fields.startDate.isYearConstant(year)
			),
			500
		).map(_.values.personId.get).toSet

		val personIDs: Set[PersonID] = expiredInYear union startedInYear

		rc.getObjectsByIds(
			Person,
			personIDs.toList,
			1000
		).toSet
	})

	def getDropdownValues(rc: RequestCache[_]): List[List[(String, String)]] = {
		val programs: List[ProgramType] = rc.getAllObjectsOfClass(ProgramType)
		List(programs.map(p =>
			(p.values.programId.get.toString, p.values.programName.get.toString)
		))
	}
}
