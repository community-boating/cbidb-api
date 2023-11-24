package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.Person

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export._
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Entities.MagicIds

class PersonFilterFactoryTag extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Has Tag"
	override val argDefinitions = List(
		(ARG_DROPDOWN, MagicIds.TAG_IDS.CORPORATION.toString),
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		implicit val rc: UnlockedRequestCache = _rc

		type PersonID = Int

		val tagId: Int = arg.toInt

		val personIDs: List[Int] = rc.getObjectsByFilters(
			PersonTag,
			List(PersonTag.fields.tagId.alias.equalsConstant(tagId)),
			Set(PersonTag.primaryKey),
			10000
		).map(_.values.personId.get)

		rc.getObjectsByIds(Person, personIDs, Set(Person.primaryKey), 10000).toSet
	})

	// TODO: exclude inactive?  Filter them to the bottom?
	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val allTags = rc.getAllObjectsOfClass(Tag, Set(Tag.primaryKey))
		List(allTags.sortWith((a, b) => a.values.tagName.get < b.values.tagName.get).map(r =>
			(r.values.tagId.get.toString, r.values.tagName.get.toString)
		))
	}
}
