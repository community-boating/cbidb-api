package Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance
import Entities.JpClassInstance
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactoryInt
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class JpClassInstanceFilterFactoryType extends ReportingFilterFactoryInt[JpClassInstance] {
  val displayName: String = "By Class Type"
  def getFilterCastArg(pb: PersistenceBroker, typeId: Int): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb
    pb.getObjectsByFilters(
      JpClassInstance,
      List(JpClassInstance.fields.typeId.equalsConstant(typeId)),
      100
    ).toSet
  })
}
