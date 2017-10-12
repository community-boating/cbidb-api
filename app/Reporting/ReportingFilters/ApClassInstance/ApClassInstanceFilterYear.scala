package Reporting.ReportingFilters.ApClassInstance

import Entities.ApClassSession
import Services.PersistenceBroker

class ApClassInstanceFilterYear(pb: PersistenceBroker, year: Int) extends ApClassInstanceFilter {
  implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
  val primaryKeyValues: Set[Int] = {
    val ss: List[ApClassSession] = pb.getObjectsByFilters(
      ApClassSession,
      List(ApClassSession.fields.sessionDateTime.isYearConstant(year)),
      1000
    )
    ss.map(s => s.values.instanceId.get).toSetgit
  }
}
