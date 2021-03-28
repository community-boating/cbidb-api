package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance

import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession}
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.{RequestCache, UnlockedRequestCache}

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
			List(JpClassSession.fields.sessionDateTime.isYearConstant(year)),
			1000
		)

		val instanceIDs = ss.map(s => s.values.instanceId.get).distinct

		rc.getObjectsByFilters(
			JpClassInstance,
			List(JpClassInstance.fields.instanceId.inList(instanceIDs)),
			1000
		).toSet
	})
}
