package Reporting.ReportFactories

import java.time.format.DateTimeFormatter

import Entities._
import Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance.{JpClassInstanceFilterFactoryType, JpClassInstanceFilterFactoryYear}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Reporting.{ReportFactory, ReportingField}
import Storable.StorableObject

class ReportFactoryJpClassInstance extends ReportFactory[JpClassInstance] {
  lazy val jpClassTypes: List[JpClassType] = pb.getObjectsByFilters(JpClassType, List(), 20)

  lazy val jpClassSessions: List[JpClassSession] = pb.getObjectsByFilters(
    JpClassSession,
    List(JpClassSession.fields.instanceId.inList(getInstances.map(i => i.values.instanceId.get))),
    1000
  )

  val entityCompanion: StorableObject[JpClassInstance] = JpClassInstance

  def decorateInstancesWithParentReferences(instances: List[JpClassInstance]): Unit = {
    val types: Map[Int, JpClassType] =
      pb.getObjectsByFilters(JpClassType, List(), 20)
        .map(t => (t.getID, t))
        .foldLeft(Map(): Map[Int, JpClassType])((m: Map[Int, JpClassType], x: (Int, JpClassType)) => m + x)

    instances.foreach(i => {
      i.setJpClassType(types(i.values.typeId.get))
    })
  }

  val FIELD_MAP: Map[String, ReportingField[JpClassInstance]] = Map(
    "TypeId" -> ReportingField.getReportingFieldFromDatabaseField(JpClassInstance.fields.typeId, "Type ID"),
    "InstanceId" -> ReportingField.getReportingFieldFromDatabaseField(JpClassInstance.fields.instanceId, "Instance ID"),
    "SessionCt" -> new ReportingField[JpClassInstance](
      (i: JpClassInstance) => jpClassSessions.count(s => s.values.instanceId.get == i.values.instanceId.get).toString,
      "Session Ct1"
    ),
    "FirstSessionDatetime" -> new ReportingField[JpClassInstance](
      (i: JpClassInstance) =>
        jpClassSessions
          .filter(_.values.instanceId.get == i.getID)
          .map(_.values.sessionDateTime.get)
          .min
          .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
      "First Session"
    ),
    "TypeDisplayOrder" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassInstance, JpClassType](
      JpClassType.fields.displayOrder,
      i => i.references.jpClassType.get,
      "TypeDisplayOrder"
    ),
    "TypeName" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassInstance, JpClassType](
      JpClassType.fields.typeName,
      i => i.references.jpClassType.get,
      "TypeName"
    )
  )

  val FILTER_MAP: Map[String, ReportingFilterFactory[JpClassInstance, _]] = Map(
    "JpClassInstanceFilterYear" -> new JpClassInstanceFilterFactoryYear(),
    "JpClassInstanceFilterType" -> new JpClassInstanceFilterFactoryType()
  )
}