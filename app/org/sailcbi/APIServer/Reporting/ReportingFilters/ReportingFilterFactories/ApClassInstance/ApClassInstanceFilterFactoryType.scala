package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassFormat, ApClassInstance, ApClassType}
import org.sailcbi.APIServer.Reporting.Report.BadReportingFilterArgumentsException
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.RequestCache

class ApClassInstanceFilterFactoryType extends ReportingFilterFactory[ApClassInstance] with ReportingFilterFactoryDropdown {
	val displayName: String = "By Class Type"
	val argDefinitions = List(
		(ARG_DROPDOWN, "1")
	)

	def getFilter(rc: RequestCache, arg: String): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(rc, (_rc: RequestCache) => {
		val typeId: Int = arg.toInt
		implicit val rc: RequestCache = _rc
		val typeID: Int = {
			val ts: List[ApClassType] = rc.getObjectsByFilters(
				ApClassType,
				List(ApClassType.fields.typeId.equalsConstant(typeId)),
				5
			)
			if (ts.size != 1) throw new BadReportingFilterArgumentsException("No such ApClassType with ID " + typeId)
			ts.head.values.typeId.get
		}

		val formatIDs: List[Int] = {
			val fs: List[ApClassFormat] = rc.getObjectsByFilters(
				ApClassFormat,
				List(ApClassFormat.fields.typeId.equalsConstant(typeID)),
				5
			)
			fs.map(f => f.values.formatId.get)
		}

		rc.getObjectsByFilters(
			ApClassInstance,
			List(ApClassInstance.fields.formatId.inList(formatIDs)),
			500
		).toSet
	})

	def getDropdownValues(rc: RequestCache): List[List[(String, String)]] = {
		val types: List[ApClassType] = rc.getAllObjectsOfClass(ApClassType)
		List(types.map(t => (t.values.typeId.get.toString, t.values.typeName.get)).sortWith((a, b) => a._2 < b._2))
	}
}