package Reporting.ReportFactories

import Entities.{ApClassFormat, ApClassInstance, ApClassSession, ApClassType}
import Reporting.ReportFactory
import Reporting.ReportingFields.{CustomReportingField, ReportingField}
import Reporting.ReportingFilters.ReportingFilter
import Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance.{ApClassInstanceFilterFactoryType, ApClassInstanceFilterFactoryYear}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Services.PersistenceBroker

class ReportFactoryApClassInstance(
  pb: PersistenceBroker, filterSpec: String, fieldSpec: String
) extends ReportFactory[ApClassInstance](pb, filterSpec, fieldSpec)
{
  val FIELD_MAP: Map[String, ReportingField[ApClassInstance]] = ReportFactoryApClassInstance.FIELD_MAP

  val FILTER_MAP: Map[String, ReportingFilterFactory[ApClassInstance]] = ReportFactoryApClassInstance.FILTER_MAP

  def decorateInstancesWithParentReferences(instances: List[ApClassInstance]): Unit = {
    val formats: Map[Int, ApClassFormat] =
      pb.getObjectsByFilters(ApClassFormat, List(), 20)
      .map(f => (f.getID, f))
      .foldLeft(Map(): Map[Int, ApClassFormat])((m: Map[Int, ApClassFormat], x: (Int, ApClassFormat)) => m + x)

    val types: Map[Int, ApClassType] =
      pb.getObjectsByFilters(ApClassType, List(), 20)
      .map(f => (f.getID, f))
      .foldLeft(Map(): Map[Int, ApClassType])((m: Map[Int, ApClassType], x: (Int, ApClassType)) => m + x)

    formats.values.foreach(f => {
      val classType = types(f.values.typeId.get)
      f.setApClassType(classType)
    })

    instances.foreach(i => {
      val classFormat = formats(i.values.formatId.get)
      i.setApClassFormat(classFormat)
    })
  }
}

object ReportFactoryApClassInstance {
  val FIELD_MAP: Map[String, ReportingField[ApClassInstance]] = Map(
    "TypeId" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassFormat](
      ApClassFormat.fields.typeId,
      i => i.references.apClassFormat.get,
      "Type ID"
    ),
    "InstanceId" -> ReportingField.getReportingFieldFromDatabaseField(ApClassInstance.fields.instanceId, "Instance ID"),
    "SessionCt" -> new CustomReportingField[ApClassInstance](
      (pb: PersistenceBroker, instances: List[ApClassInstance]) => {
        val sessions: List[ApClassSession] = pb.getObjectsByFilters(
          ApClassSession,
          List(ApClassSession.fields.instanceId.inList(instances.map(i => i.values.instanceId.get))),
          100
        )
        (i: ApClassInstance) => sessions.count(s => s.values.instanceId.get == i.values.instanceId.get).toString
      },
      "Session Ct1"
    )
  )

  val FILTER_MAP: Map[String, ReportingFilterFactory[ApClassInstance]] = Map(
    "ApClassInstanceFilterYear" -> new ApClassInstanceFilterFactoryYear(),
    "ApClassInstanceFilterType" -> new ApClassInstanceFilterFactoryType()
  )
}