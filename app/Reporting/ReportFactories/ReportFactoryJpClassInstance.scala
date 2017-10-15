package Reporting.ReportFactories

import Entities._
import Reporting.Report.BadReportingFilterArgumentsException
import Reporting.ReportFactory
import Reporting.ReportingFields.{CustomReportingField, ReportingField}
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class ReportFactoryJpClassInstance(
  pb: PersistenceBroker, filterSpec: String, fieldSpec: String
) extends ReportFactory[JpClassInstance](pb, filterSpec, fieldSpec)
{
  val FIELD_MAP: Map[String, ReportingField[JpClassInstance]] = ReportFactoryJpClassInstance.FIELD_MAP

  def getFilterMap(pb: PersistenceBroker): Map[String, String => ReportingFilter[JpClassInstance]] = ReportFactoryJpClassInstance.getFilterMap(pb)

  def decorateInstancesWithParentReferences(instances: List[JpClassInstance]): Unit = {
    val types: Map[Int, JpClassType] =
      pb.getObjectsByFilters(JpClassType, List(), 20)
        .map(t => (t.getID, t))
        .foldLeft(Map(): Map[Int, JpClassType])((m: Map[Int, JpClassType], x: (Int, JpClassType)) => m + x)

    instances.foreach(i => {
      i.setJpClassType(types(i.values.typeId.get))
    })
  }
}

object ReportFactoryJpClassInstance {
  val FIELD_MAP: Map[String, ReportingField[JpClassInstance]] = Map(
    "TypeId" -> ReportingField.getReportingFieldFromDatabaseField(JpClassInstance.fields.instanceId, "Type ID"),
    "InstanceId" -> ReportingField.getReportingFieldFromDatabaseField(JpClassInstance.fields.instanceId, "Instance ID"),
    "SessionCt" -> new CustomReportingField[JpClassInstance](
      (pb: PersistenceBroker, instances: List[JpClassInstance]) => {
        val sessions: List[JpClassSession] = pb.getObjectsByFilters(
          JpClassSession,
          List(JpClassSession.fields.instanceId.inList(instances.map(i => i.values.instanceId.get))),
          100
        )
        (i: JpClassInstance) => sessions.count(s => s.values.instanceId.get == i.values.instanceId.get).toString
      },
      "Session Ct1"
    ),
    "TypeDisplayOrder" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassInstance, JpClassType](
      JpClassType.fields.displayOrder,
      i => i.references.jpClassType.get,
      "TypeDisplayOrder"
    ),
    "TypeName" -> ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassInstance, JpClassType](
      JpClassType.fields.typeName,
      i => i.references.jpClassType.get,
      "TypeDisplayOrder"
    )
  )

  def getFilterMap(pb: PersistenceBroker): Map[String, String => ReportingFilter[JpClassInstance]] = Map(
    "JpClassInstanceFilterYear" -> ((args: String) => new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
      implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
      val ss: List[JpClassSession] = pb.getObjectsByFilters(
        JpClassSession,
        List(JpClassSession.fields.sessionDateTime.isYearConstant(args.toInt)),
        1000
      )
      val instanceIDs = ss.map(s => s.values.instanceId.get).distinct

      pb.getObjectsByFilters(
        JpClassInstance,
        List(JpClassInstance.fields.instanceId.inList(instanceIDs)),
        1000
      ).toSet
    })),
    "JpClassInstanceFilterType" -> ((args: String) => new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
      pb.getObjectsByFilters(
        JpClassInstance,
        List(JpClassInstance.fields.typeId.equalsConstant(args.toInt)),
        100
      ).toSet
    }))
  )
}