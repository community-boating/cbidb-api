package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.JpClassInstance

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export._
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassType}

class JpClassInstanceFilterFactoryType extends ReportingFilterFactory[JpClassInstance] with ReportingFilterFactoryDropdown {
	val displayName: String = "By Class Type"
	val argDefinitions = List(
		(ARG_DROPDOWN, "1")
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		val typeId = arg.toInt
		implicit val rc: UnlockedRequestCache = _rc
		rc.getObjectsByFilters(
			JpClassInstance,
			List(JpClassInstance.fields.typeId.equalsConstant(typeId)),
			Set.empty,
			100
		).toSet
	})

	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]] = {
		val types: List[JpClassType] = rc.getAllObjectsOfClass(JpClassType)
		List(types.map(t => (t.values.typeId.get.toString, t.values.typeName.get)).sortWith((a, b) => a._2 < b._2))
	}
}
