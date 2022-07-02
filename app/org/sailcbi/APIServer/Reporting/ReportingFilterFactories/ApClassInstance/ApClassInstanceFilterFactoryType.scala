package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.ApClassInstance

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export.Report.BadReportingFilterArgumentsException
import com.coleji.neptune.Export._
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
				List(ApClassType.fields.typeId.alias.equalsConstant(typeId)),
				Set(ApClassType.primaryKey),
				5
			)
			if (ts.size != 1) throw new BadReportingFilterArgumentsException("No such ApClassType with ID " + typeId)
			ts.head.values.typeId.get
		}

		val formatIDs: List[Int] = {
			val fs: List[ApClassFormat] = rc.getObjectsByFilters(
				ApClassFormat,
				List(ApClassFormat.fields.typeId.alias.equalsConstant(typeID)),
				Set(ApClassFormat.primaryKey),
				5
			)
			fs.map(f => f.values.formatId.get)
		}

		rc.getObjectsByFilters(
			ApClassInstance,
			List(ApClassInstance.fields.formatId.alias.inList(formatIDs)),
			Set(ApClassInstance.primaryKey),
			500
		).toSet
	})

	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val types: List[ApClassType] = rc.getAllObjectsOfClass(ApClassType, Set(ApClassType.primaryKey))
		List(types.map(t => (t.values.typeId.get.toString, t.values.typeName.get)).sortWith((a, b) => a._2 < b._2))
	}
}