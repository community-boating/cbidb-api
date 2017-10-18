package Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance
import Entities.JpClassInstance
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class JpClassInstanceFilterFactoryType extends ReportingFilterFactory[JpClassInstance] {
  val displayName: String = "By Class Type"
  def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(pb, (pb: PersistenceBroker) => {
    pb.getObjectsByFilters(
      JpClassInstance,
      List(JpClassInstance.fields.typeId.equalsConstant(args.toInt)),
      100
    ).toSet
  })
}
