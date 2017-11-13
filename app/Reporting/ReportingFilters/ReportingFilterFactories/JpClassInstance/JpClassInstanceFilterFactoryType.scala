package Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance
import Entities.{JpClassInstance, JpClassType}
import Reporting.ReportingFilters.ReportingFilterFactories.{ReportingFilterFactoryDropdown, ReportingFilterFactoryInt}
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction}
import Services.PersistenceBroker

class JpClassInstanceFilterFactoryType extends ReportingFilterFactoryInt[JpClassInstance] with ReportingFilterFactoryDropdown {
  val displayName: String = "By Class Type"
  val defaultValue: String = "1"
  def getFilterCastArg(pb: PersistenceBroker, typeId: Int): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    implicit val pb: PersistenceBroker = _pb
    pb.getObjectsByFilters(
      JpClassInstance,
      List(JpClassInstance.fields.typeId.equalsConstant(typeId)),
      100
    ).toSet
  })

  def getDropdownValues(pb: PersistenceBroker): List[(String, String)] = {
    val types: List[JpClassType] = pb.getAllObjectsOfClass(JpClassType)
    types.map(t => (t.values.typeId.get.toString, t.values.typeName.get)).sortWith((a, b) => a._2 < b._2)
  }
}
