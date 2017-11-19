package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.{ApClassInstance, ApClassSession}
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class ApClassInstanceFilterFactoryYear extends ReportingFilterFactory[ApClassInstance] {
  val argTypes: List[ReportingFilterArgType] = List(ARG_INT)
  val displayName: String = "By Season"
  // TODO: replace with current calendar/season year
  val defaultValue: String = "2017"
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    val year: Int = arg.toInt
    implicit val pb: PersistenceBroker = _pb
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