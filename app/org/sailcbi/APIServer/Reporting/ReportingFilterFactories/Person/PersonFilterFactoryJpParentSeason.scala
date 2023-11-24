package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.Person


import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export._
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.MagicIds
import org.sailcbi.APIServer.Logic.DateLogic

class PersonFilterFactoryJpParentSeason extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "JP Parent in Season"
	val argDefinitions = List(
		(ARG_DROPDOWN, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		implicit val rc: UnlockedRequestCache = _rc

		type PersonID = Int

		val season = arg.toInt

		val juniorsThatYear = rc.getObjectsByFilters(
			PersonMembership,
			List(
				PersonMembership.fields.membershipTypeId.alias.equalsConstant(MagicIds.MEMBERSHIP_TYPES.JUNIOR_SUMMER),
				PersonMembership.fields.expirationDate.alias.isYearConstant(season)
			),
			Set(PersonMembership.primaryKey)
		).map(_.values.personId.get)

		val parentIds = rc.getObjectsByFilters(
			PersonRelationship,
			List(
				PersonRelationship.fields.typeId.alias.equalsConstant(MagicIds.PERSON_RELATIONSHIP_TYPE_PARENT_WITH_ACCT_LINK),
				PersonRelationship.fields.b.alias.inList(juniorsThatYear)
			),
			Set(PersonRelationship.primaryKey)
		).map(_.values.a.get)

		rc.getObjectsByIds(Person, parentIds, Set(Person.primaryKey)).toSet
	})

	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val GO_BACK = 5
		val currentSeason = DateLogic.currentSeason()
		val start = currentSeason - GO_BACK
		List(
			(start to currentSeason).toList.map(_.toString).map(i => (i, i))
		)
	}
}
