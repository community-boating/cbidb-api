package Reporting.ReportFactories

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}

import Entities._
import Reporting.ReportFactory
import Reporting.ReportingFields.{CustomReportingField, ReportingField}
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
    ),
    "FirstSessionDatetime" -> new CustomReportingField[ApClassInstance](
      (pb: PersistenceBroker, instances: List[ApClassInstance]) => {
        implicit val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(
          (d: LocalDateTime) => d.atZone(ZoneId.systemDefault).toInstant.toEpochMilli
        )
        val sessions: List[ApClassSession] = pb.getObjectsByFilters(
          ApClassSession,
          List(ApClassSession.fields.instanceId.inList(instances.map(i => i.values.instanceId.get))),
          100
        )
        (i: ApClassInstance) =>
          sessions
            .filter(_.values.instanceId.get == i.getID)
            .map(_.values.sessionDateTime.get)
            .min
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
      },
      "First Session"
    ),
    "TypeName" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassType](
      ApClassType.fields.typeName,
      i => i.references.apClassFormat.get.references.apClassType.get,
      "TypeName"
    ),
    "TypeDisplayOrder" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassType](
      ApClassType.fields.displayOrder,
      i => i.references.apClassFormat.get.references.apClassType.get,
      "TypeDisplayOrder"
    )
  )

  val FILTER_MAP: Map[String, ReportingFilterFactory[ApClassInstance]] = Map(
    "ApClassInstanceFilterYear" -> new ApClassInstanceFilterFactoryYear(),
    "ApClassInstanceFilterType" -> new ApClassInstanceFilterFactoryType()
  )
}