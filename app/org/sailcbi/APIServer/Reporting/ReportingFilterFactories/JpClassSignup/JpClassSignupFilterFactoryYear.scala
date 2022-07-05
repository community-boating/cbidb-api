package org.sailcbi.APIServer.Reporting.ReportingFilterFactories.JpClassSignup

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Export.{ARG_INT, ReportingFilter, ReportingFilterFactory, ReportingFilterFunction}
import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassSession, JpClassSignup}
import org.sailcbi.APIServer.Logic.DateLogic

class JpClassSignupFilterFactoryYear extends ReportingFilterFactory[JpClassSignup] {
	val displayName: String = "By Season"
	val argDefinitions = List(
		(ARG_INT, DateLogic.currentSeason().toString)
	)

	def getFilter(rc: UnlockedRequestCache, arg: String): ReportingFilter[JpClassSignup] = new ReportingFilterFunction(rc, (_rc: UnlockedRequestCache) => {
		val year = arg.toInt
		implicit val rc: UnlockedRequestCache = _rc
		val sessions = rc.getObjectsByFilters(
			JpClassSession,
			List(JpClassSession.fields.sessionDateTime.alias.isYearConstant(year)),
			Set(JpClassSession.primaryKey),
			100
		)
		val instanceIDs = sessions.map(_.values.instanceId.get).toSet
		rc.getObjectsByFilters(
			JpClassSignup,
			List(JpClassSignup.fields.instanceId.alias.inList(instanceIDs.toList)),
			Set(JpClassSignup.primaryKey),
			100
		).toSet
	})
}
