package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.Person

import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.RequestCache

class PersonFilterFactoryTag extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Has Tag"
	val argDefinitions = List(
		(ARG_DROPDOWN, Tag.specialIDs.TAG_ID_CORPORATION.toString),
	)

	def getFilter(rc: RequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: RequestCache) => {
		implicit val rc: RequestCache = _rc

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
	def getDropdownValues(rc: RequestCache): List[List[(String, String)]] = {
		val allTags = rc.getAllObjectsOfClass(Tag)
		List(allTags.sortWith((a, b) => a.values.tagName.get < b.values.tagName.get).map(r =>
			(r.values.tagId.get.toString, r.values.tagName.get.toString)
		))
	}
}
