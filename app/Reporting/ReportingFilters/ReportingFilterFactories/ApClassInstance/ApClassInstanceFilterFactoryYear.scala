package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.EntityDefinitions.{ApClassInstance, ApClassSession}
import Reporting.ReportingFilters._
import Services.PersistenceBroker
import Services.ServerBootLoader.ssc

class ApClassInstanceFilterFactoryYear extends ReportingFilterFactory[ApClassInstance] {
  val displayName: String = "By Season"
  val argDefinitions = List(
    (ARG_INT, ssc.currentSeason().toString)
  )
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