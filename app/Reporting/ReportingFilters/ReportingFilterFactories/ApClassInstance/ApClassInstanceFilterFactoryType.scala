package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Reporting.Report.BadReportingFilterArgumentsException
import Reporting.ReportingFilters.ReportingFilterFactories.{ReportingFilterFactory, ReportingFilterFactoryInt}
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class ApClassInstanceFilterFactoryType extends ReportingFilterFactoryInt[ApClassInstance] {
  val displayName: String = "By Class Type"
  def getFilterCastArg(pb: PersistenceBroker, typeId: Int): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
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
}