package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.ApClassInstance

import com.coleji.framework.Core.UnlockedRequestCache
import com.coleji.framework.Export.Report.BadReportingFilterArgumentsException
import com.coleji.framework.Export.{ARG_DROPDOWN, ReportingFilter, ReportingFilterFactory, ReportingFilterFactoryDropdown, ReportingFilterFunction}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassFormat, ApClassInstance, ApClassType}

class ApClassInstanceFilterFactoryType extends ReportingFilterFactory[ApClassInstance] with ReportingFilterFactoryDropdown {
	val displayName: String = "By Class Type"
	val argDefinitions = List(
		(ARG_DROPDOWN, "1")
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		val typeId: Int = arg.toInt
		implicit val rc: UnlockedRequestCache = _rc
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

	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val types: List[ApClassType] = rc.getAllObjectsOfClass(ApClassType)
		List(types.map(t => (t.values.typeId.get.toString, t.values.typeName.get)).sortWith((a, b) => a._2 < b._2))
	}
}