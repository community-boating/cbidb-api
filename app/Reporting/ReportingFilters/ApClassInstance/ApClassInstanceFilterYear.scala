package Reporting.ReportingFilters.ApClassInstance

import Entities.{ApClassInstance, ApClassSession}
import Services.PersistenceBroker

class ApClassInstanceFilterYear(pb: PersistenceBroker, year: Int) extends ApClassInstanceFilter {
  implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
  val instances: Set[ApClassInstance] = {
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
  }
}
