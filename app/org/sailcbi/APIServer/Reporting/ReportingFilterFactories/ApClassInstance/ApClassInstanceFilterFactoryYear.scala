package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.ApClassInstance

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export.{ARG_INT, ReportingFilter, ReportingFilterFactory, ReportingFilterFunction}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassInstance, ApClassSession}
import org.sailcbi.APIServer.Logic.DateLogic

class ApClassInstanceFilterFactoryYear extends ReportingFilterFactory[ApClassInstance] {
	val displayName: String = "By Season"
	val argDefinitions = List(
		(ARG_INT, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		val year: Int = arg.toInt
		implicit val rc: UnlockedRequestCache = _rc
		val ss: List[ApClassSession] = rc.getObjectsByFilters(
			ApClassSession,
			List(ApClassSession.fields.sessionDateTime.alias.isYearConstant(year)),
			Set(ApClassSession.primaryKey),
			1000
		)
		val instanceIDs = ss.map(s => s.values.instanceId.get)

		rc.getObjectsByFilters(
			ApClassInstance,
			List(ApClassInstance.fields.instanceId.alias.inList(instanceIDs)),
			Set(ApClassInstance.primaryKey),
			1000
		).toSet
	})
}