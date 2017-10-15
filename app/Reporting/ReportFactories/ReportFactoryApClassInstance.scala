package Reporting.ReportFactories

import Entities.{ApClassFormat, ApClassInstance, ApClassSession, ApClassType}
import Reporting.Report.BadReportingFilterArgumentsException
import Reporting.ReportFactory
import Reporting.ReportingFields.{CustomReportingField, ReportingField}
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class ReportFactoryApClassInstance(
  pb: PersistenceBroker, filterSpec: String, fieldSpec: String
) extends ReportFactory[ApClassInstance](pb, filterSpec, fieldSpec)
{
  val FIELD_MAP: Map[String, ReportingField[ApClassInstance]] = ReportFactoryApClassInstance.FIELD_MAP

  def getFilterMap(pb: PersistenceBroker): Map[String, String => ReportingFilter[ApClassInstance]] = ReportFactoryApClassInstance.getFilterMap(pb)

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

  def getFilterMap(pb: PersistenceBroker): Map[String, String => ReportingFilter[ApClassInstance]] = Map(
    "ApClassInstanceFilterYear" -> ((args: String) => new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
      implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
      val ss: List[ApClassSession] = pb.getObjectsByFilters(
        ApClassSession,
        List(ApClassSession.fields.sessionDateTime.isYearConstant(args.toInt)),
        1000
      )
      val instanceIDs = ss.map(s => s.values.instanceId.get)

      pb.getObjectsByFilters(
        ApClassInstance,
        List(ApClassInstance.fields.instanceId.inList(instanceIDs)),
        1000
      ).toSet
    })),
    "ApClassInstanceFilterType" -> ((args: String) => new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
      val typeID: Int = {
        val ts: List[ApClassType] = pb.getObjectsByFilters(
          ApClassType,
          List(ApClassType.fields.typeId.equalsConstant(args.toInt)),
          5
        )
        if (ts.size != 1) throw new BadReportingFilterArgumentsException("No such ApClassType with ID " + args)
        ts.head.values.typeId.get
      }

      val formatIDs: List[Int] = {
        val fs: List[ApClassFormat] = pb.getObjectsByFilters(
          ApClassFormat,
          List(ApClassFormat.fields.typeId.equalsConstant(typeID)),
          5
        )
        fs.map(f => f.values.formatId.get)
      }

      pb.getObjectsByFilters(
        ApClassInstance,
        List(ApClassInstance.fields.formatId.inList(formatIDs)),
        100
      ).toSet
    }))
  )
}