package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Reporting.Report.BadReportingFilterArgumentsException
import Reporting.ReportingFilters.ReportingFilterFactories.{ReportingFilterFactoryDropdown, ReportingFilterFactoryInt}
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class ApClassInstanceFilterFactoryType extends ReportingFilterFactoryInt[ApClassInstance] with ReportingFilterFactoryDropdown {
  val displayName: String = "By Class Type"
  def getFilterCastArg(pb: PersistenceBroker, typeId: Int): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb
    val typeID: Int = {
      val ts: List[ApClassType] = pb.getObjectsByFilters(
        ApClassType,
        List(ApClassType.fields.typeId.equalsConstant(typeId)),
        5
      )
      if (ts.size != 1) throw new BadReportingFilterArgumentsException("No such ApClassType with ID " + typeId)
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
      500
    ).toSet
  })

  def getDropdownValues(pb: PersistenceBroker): List[(String, String)] = {
    val types: List[ApClassType] = pb.getAllObjectsOfClass(ApClassType)
    types.map(t => (t.values.typeId.get.toString, t.values.typeName.get)).sortWith((a, b) => a._2 < b._2)
  }
}