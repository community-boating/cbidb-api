package Reporting.ReportingFilters.ReportingFilterFactories.ApClassInstance

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Reporting.Report.BadReportingFilterArgumentsException
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class ApClassInstanceFilterFactoryType extends ReportingFilterFactory[ApClassInstance] {
  def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[ApClassInstance] = new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
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
      500
    ).toSet
  })
}