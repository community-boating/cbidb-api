package Reporting.ReportingFilters.ReportingFilterFactories.Person

import Entities.EntityDefinitions._
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class PersonFilterFactoryTag extends ReportingFilterFactory[Person] with ReportingFilterFactoryDropdown {
	val displayName: String = "Has Tag"
	val argDefinitions = List(
		(ARG_DROPDOWN, Tag.specialIDs.TAG_ID_CORPORATION.toString),
	)

	def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[Person] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
		implicit val pb: PersistenceBroker = _pb

		type PersonID = Int

		val tagId: Int = arg.toInt

		val personIDs: List[Int] = pb.getObjectsByFilters(
			PersonTag,
			List(PersonTag.fields.tagId.equalsConstant(tagId)),
			10000
		).map(_.values.personId.get)

		pb.getObjectsByIds(Person, personIDs, 10000).toSet
	})

	// TODO: exclude inactive?  Filter them to the bottom?
	def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
		val allTags = pb.getAllObjectsOfClass(Tag)
		List(allTags.sortWith((a, b) => a.values.tagName.get < b.values.tagName.get).map(r =>
			(r.values.tagId.get.toString, r.values.tagName.get.toString)
		))
	}
}
