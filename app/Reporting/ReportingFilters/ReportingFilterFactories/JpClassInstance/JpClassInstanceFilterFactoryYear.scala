package Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance

import Entities.{JpClassInstance, JpClassSession}
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class JpClassInstanceFilterFactoryYear extends ReportingFilterFactory[JpClassInstance] {
  val argTypes: List[ReportingFilterArgType] = List(ARG_INT)
  val displayName: String = "By Season"
  // TODO: replace with current calendar/season year
  val defaultValue: String = "2017"
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
