package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.Person

import com.coleji.framework.Export.{ARG_DROPDOWN, ReportingFilter, ReportingFilterFactory, ReportingFilterFactoryDropdown, ReportingFilterFunction}
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Services.{RequestCache, UnlockedRequestCache}

class PersonFilterFactoryTag extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Has Tag"
	val argDefinitions = List(
		(ARG_DROPDOWN, Tag.specialIDs.TAG_ID_CORPORATION.toString),
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		implicit val rc: UnlockedRequestCache = _rc

		type PersonID = Int

		val tagId: Int = arg.toInt

		val personIDs: List[Int] = rc.getObjectsByFilters(
			PersonTag,
			List(PersonTag.fields.tagId.equalsConstant(tagId)),
			10000
		).map(_.values.personId.get)

		rc.getObjectsByIds(Person, personIDs, 10000).toSet
	})

	// TODO: exclude inactive?  Filter them to the bottom?
	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val allTags = rc.getAllObjectsOfClass(Tag)
		List(allTags.sortWith((a, b) => a.values.tagName.get < b.values.tagName.get).map(r =>
			(r.values.tagId.get.toString, r.values.tagName.get.toString)
		))
	}
}
