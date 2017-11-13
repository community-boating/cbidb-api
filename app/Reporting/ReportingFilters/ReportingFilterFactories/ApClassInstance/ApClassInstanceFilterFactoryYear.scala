package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.{ApClassInstance, ApClassSession}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactoryInt
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class ApClassInstanceFilterFactoryYear extends ReportingFilterFactoryInt[ApClassInstance] {
  val displayName: String = "By Season"
  // TODO: replace with current calendar/season year
  val defaultValue: String = "2017"
  def getFilterCastArg(pb: PersistenceBroker, year: Int): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
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