package Reporting.ReportingFields.ApClassInstance

import Entities.{ApClassInstance, ApClassSession}
import Reporting.ReportingFields.ReportingField
import Services.PersistenceBroker

class ApClassInstanceReportingFieldSessionCount(fieldDisplayName: String) extends ReportingField[ApClassInstance](fieldDisplayName) {
  def getValueFunction(pb: PersistenceBroker, instances: List[ApClassInstance]): ApClassInstance => String = {
    val sessions: List[ApClassSession] = pb.getObjectsByFilters(
      ApClassSession,
      List(ApClassSession.fields.instanceId.inList(instances.map(i => i.values.instanceId.get))),
      100
    )
    (i: ApClassInstance) => sessions.count(s => s.values.instanceId.get == i.values.instanceId.get).toString
  }
}
