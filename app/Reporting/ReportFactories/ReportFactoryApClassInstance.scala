package Reporting.ReportFactories

import java.time.format.DateTimeFormatter

import Entities._
import Reporting.{ReportFactory, ReportingField}
import Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance.{ApClassInstanceFilterFactoryType, ApClassInstanceFilterFactoryYear}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Services.PersistenceBroker

class ReportFactoryApClassInstance extends ReportFactory[ApClassInstance] {
  lazy val apClassFormats: List[ApClassFormat] = pb.getObjectsByFilters(ApClassFormat, List(), 20)

  lazy val apClassTypes: List[ApClassType] = pb.getObjectsByFilters(ApClassType, List(), 20)

  lazy val apClassSessions: List[ApClassSession] = pb.getObjectsByFilters(
    ApClassSession,
    List(ApClassSession.fields.instanceId.inList(getInstances.map(i => i.values.instanceId.get))),
    1000
  )

  def decorateInstancesWithParentReferences(instances: List[ApClassInstance]): Unit = {
    val formats: Map[Int, ApClassFormat] =
      apClassFormats
      .map(f => (f.getID, f))
      .foldLeft(Map(): Map[Int, ApClassFormat])((m: Map[Int, ApClassFormat], x: (Int, ApClassFormat)) => m + x)

    val types: Map[Int, ApClassType] =
      apClassTypes
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

  val FIELD_MAP: Map[String, ReportingField[ApClassInstance]] = Map(
    "TypeId" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassFormat](
      ApClassFormat.fields.typeId,
      i => i.references.apClassFormat.get,
      "Type ID"
    ),
    "InstanceId" -> ReportingField.getReportingFieldFromDatabaseField(ApClassInstance.fields.instanceId, "Instance ID"),
    "SessionCt" -> new ReportingField[ApClassInstance](
      (i: ApClassInstance) => apClassSessions.count(s => s.values.instanceId.get == i.values.instanceId.get).toString,
      "Session Ct1"
    ),
    "FirstSessionDatetime" -> new ReportingField[ApClassInstance](
      (i: ApClassInstance) =>
        apClassSessions
          .filter(_.values.instanceId.get == i.getID)
          .map(_.values.sessionDateTime.get)
          .min
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
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