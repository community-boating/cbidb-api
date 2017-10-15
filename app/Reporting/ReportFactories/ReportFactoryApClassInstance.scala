package Reporting.ReportFactories

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Reporting.ReportFactory
import Reporting.ReportingFields.ApClassInstance.ApClassInstanceReportingFieldSessionCount
import Reporting.ReportingFields.ReportingField
import Services.PersistenceBroker

class ReportFactoryApClassInstance(
  pb: PersistenceBroker, filterSpec: String, fieldSpec: String
) extends ReportFactory[ApClassInstance](pb, filterSpec, fieldSpec)
{
  def getFields: List[ReportingField[ApClassInstance]] = List(
    ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassFormat](
      ApClassFormat.fields.typeId,
      i => i.references.apClassFormat.get,
      "Type ID"
    ),
    ReportingField.getReportingFieldFromDatabaseField(ApClassInstance.fields.instanceId, "Instance ID"),
    new ApClassInstanceReportingFieldSessionCount("Session Ct")
  )

  def decorateInstancesWithParentReferences(instances: List[ApClassInstance]): Unit = {
    instances.foreach(i => {
      pb.getObjectById(ApClassFormat, i.values.formatId.get) match {
        case Some(f: ApClassFormat) => i.setApClassFormat(f)
      }
      val format: ApClassFormat = i.references.apClassFormat.get
      pb.getObjectById(ApClassType, format.values.typeId.get) match {
        case Some(t: ApClassType) => format.setApClassType(t)
      }
    })
  }
}

object ReportFactoryApClassInstance {
  object filters {

  }

  object fields {

  }
}