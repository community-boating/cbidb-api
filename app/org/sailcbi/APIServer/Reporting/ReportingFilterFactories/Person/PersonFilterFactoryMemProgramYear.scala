package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.Person

import com.coleji.framework.Core.UnlockedRequestCache
import com.coleji.framework.Export._
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Logic.DateLogic

// First arg is program
// second arg is year
class PersonFilterFactoryMemProgramYear extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Had mem in prog X, year Y"
	val argDefinitions = List(
		(ARG_DROPDOWN, ProgramType.specialIDs.PROGRAM_TYPE_ID_AP.toString),
		(ARG_INT, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		implicit val rc: UnlockedRequestCache = _rc

		type PersonID = Int

		val twoArgs = arg.split(",")
		val programId = twoArgs(0).toInt
		val year = twoArgs(1).toInt
		println("programId is " + programId + " and year is " + year)

		val membershipTypeIDs = rc.getObjectsByFilters(
			MembershipType,
			List(MembershipType.fields.programId.equalsConstant(programId)),
			Set.empty,
			15
		).map(_.values.membershipTypeId.get)

		val expiredInYear: Set[PersonID] = rc.getObjectsByFilters(
			PersonMembership,
			List(
				PersonMembership.fields.membershipTypeId.inList(membershipTypeIDs),
				PersonMembership.fields.expirationDate.isYearConstant(year)
			),
			Set.empty,
			500
		).map(_.values.personId.get).toSet

		val startedInYear: Set[PersonID] = rc.getObjectsByFilters(
			PersonMembership,
			List(
				PersonMembership.fields.membershipTypeId.inList(membershipTypeIDs),
				PersonMembership.fields.startDate.isYearConstant(year)
			),
			Set.empty,
			500
		).map(_.values.personId.get).toSet

		val personIDs: Set[PersonID] = expiredInYear union startedInYear

		rc.getObjectsByIds(
			Person,
			personIDs.toList,
			1000
		).toSet
	})

	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val programs: List[ProgramType] = rc.getAllObjectsOfClass(ProgramType)
		List(programs.map(p =>
			(p.values.programId.get.toString, p.values.programName.get.toString)
		))
	}
}
