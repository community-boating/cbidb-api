package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import org.sailcbi.APIServer.Entities.EntityDefinitions.{ApClassInstance, ApClassSession}
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.RequestCache

class ApClassInstanceFilterFactoryYear extends ReportingFilterFactory[ApClassInstance] {
	val displayName: String = "By Season"
	val argDefinitions = List(
		(ARG_INT, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: RequestCache[_], arg: String): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(rc, (_rc: RequestCache[_]) => {
		val year: Int = arg.toInt
		implicit val rc: RequestCache[_] = _rc
		val ss: List[ApClassSession] = rc.getObjectsByFilters(
			ApClassSession,
			List(ApClassSession.fields.sessionDateTime.isYearConstant(year)),
			1000
		)
		val instanceIDs = ss.map(s => s.values.instanceId.get)

		rc.getObjectsByFilters(
			ApClassInstance,
			List(ApClassInstance.fields.instanceId.inList(instanceIDs)),
			1000
		).toSet
	})
}