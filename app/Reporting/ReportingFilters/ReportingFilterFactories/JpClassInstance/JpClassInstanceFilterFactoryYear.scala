package Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance

import Entities.{JpClassInstance, JpClassSession}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class JpClassInstanceFilterFactoryYear extends ReportingFilterFactory[JpClassInstance] {
  def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
    implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
    val ss: List[JpClassSession] = pb.getObjectsByFilters(
      JpClassSession,
      List(JpClassSession.fields.sessionDateTime.isYearConstant(args.toInt)),
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
