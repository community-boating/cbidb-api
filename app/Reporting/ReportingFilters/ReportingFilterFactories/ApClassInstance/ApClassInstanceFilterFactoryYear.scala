package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.{ApClassInstance, ApClassSession}
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Services.PersistenceBroker

class ApClassInstanceFilterFactoryYear extends ReportingFilterFactory[ApClassInstance] {
  def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
    implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
    val ss: List[ApClassSession] = pb.getObjectsByFilters(
      ApClassSession,
      List(ApClassSession.fields.sessionDateTime.isYearConstant(args.toInt)),
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