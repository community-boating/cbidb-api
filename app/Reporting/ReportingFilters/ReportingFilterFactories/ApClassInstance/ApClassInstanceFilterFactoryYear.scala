package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.{ApClassInstance, ApClassSession}
import Reporting.ReportingFilters.ReportingFilterFactories.{ReportingFilterFactory, ReportingFilterFactoryInt}
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class ApClassInstanceFilterFactoryYear extends ReportingFilterFactoryInt[ApClassInstance] {
  val displayName: String = "By Season"
  def getFilterCastArg(pb: PersistenceBroker, year: Int): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
    implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
    val ss: List[ApClassSession] = pb.getObjectsByFilters(
      ApClassSession,
      List(ApClassSession.fields.sessionDateTime.isYearConstant(year)),
      1000
    )
    val instanceIDs = ss.map(s => s.values.instanceId.get)

    pb.getObjectsByFilters(
      ApClassInstance,
      List(ApClassInstance.fields.instanceId.inList(instanceIDs)),
      1000
    ).toSet
  })
}