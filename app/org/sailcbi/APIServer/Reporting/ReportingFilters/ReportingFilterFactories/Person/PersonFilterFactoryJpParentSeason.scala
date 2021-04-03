package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.Person


import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.RequestCache

class PersonFilterFactoryJpParentSeason extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "JP Parent in Season"
	val argDefinitions = List(
		(ARG_DROPDOWN, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: RequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: RequestCache) => {
		implicit val rc: RequestCache = _rc

		type PersonID = Int

		val season = arg.toInt

		val juniorsThatYear = rc.getObjectsByFilters(
			PersonMembership,
			List(
				PersonMembership.fields.membershipTypeId.equalsConstant(MembershipType.specialIDs.MEM_TYPE_ID_JUNIOR_SUMMER),
				PersonMembership.fields.expirationDate.isYearConstant(season)
			)
		).map(_.values.personId.get)

		val parentIds = rc.getObjectsByFilters(
			PersonRelationship,
			List(
				PersonRelationship.fields.typeId.equalsConstant(PersonRelationship.specialIDs.TYPE_ID_PARENT_CHILD_ACCT_LINKED),
				PersonRelationship.fields.b.inList(juniorsThatYear)
			)
		).map(_.values.a.get)

		rc.getObjectsByIds(Person, parentIds).toSet
	})

	def getDropdownValues(rc: RequestCache): List[List[(String, String)]] = {
		val GO_BACK = 5
		val currentSeason = DateLogic.currentSeason()
		val start = currentSeason - GO_BACK
		List(
			(start to currentSeason).toList.map(_.toString).map(i => (i, i))
		)
	}
}