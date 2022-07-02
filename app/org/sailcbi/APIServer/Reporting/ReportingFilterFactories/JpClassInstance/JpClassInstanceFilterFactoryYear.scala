package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.JpClassInstance

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export.{ARG_INT, ReportingFilter, ReportingFilterFactory, ReportingFilterFunction}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession}
import org.sailcbi.APIServer.Logic.DateLogic

class JpClassInstanceFilterFactoryYear extends ReportingFilterFactory[JpClassInstance] {
	val displayName: String = "By Season"
	val argDefinitions = List(
		(ARG_INT, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		val year = arg.toInt
		implicit val rc: UnlockedRequestCache = _rc
		val ss: List[JpClassSession] = rc.getObjectsByFilters(
			JpClassSession,
			List(JpClassSession.fields.sessionDateTime.alias.isYearConstant(year)),
			Set(JpClassSession.primaryKey),
			1000
		)

		val instanceIDs = ss.map(s => s.values.instanceId.get).distinct

		rc.getObjectsByFilters(
			JpClassInstance,
			List(JpClassInstance.fields.instanceId.alias.inList(instanceIDs)),
			Set(JpClassInstance.primaryKey),
			1000
		).toSet
	})
}
