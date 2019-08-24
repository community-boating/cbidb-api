package org.sailcbi.APIServer.Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance

import org.sailcbi.APIServer.Entities.EntityDefinitions.{JpClassInstance, JpClassSession}
import org.sailcbi.APIServer.Logic.DateLogic
import org.sailcbi.APIServer.Reporting.ReportingFilters._
import org.sailcbi.APIServer.Services.PersistenceBroker

class JpClassInstanceFilterFactoryYear extends ReportingFilterFactory[JpClassInstance] {
	val displayName: String = "By Season"
	val argDefinitions = List(
		(ARG_INT, DateLogic.currentSeason().toString)
	)

	def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
		val year = arg.toInt
		implicit val pb: PersistenceBroker = _pb
		val ss: List[JpClassSession] = pb.getObjectsByFilters(
			JpClassSession,
			List(JpClassSession.fields.sessionDateTime.isYearConstant(year)),
			1000
		)

		val instanceIDs = ss.map(s => s.values.instanceId.get).distinct

		pb.getObjectsByFilters(
			JpClassInstance,
			List(JpClassInstance.fields.instanceId.inList(instanceIDs)),
			1000
		).toSet
	})
}
