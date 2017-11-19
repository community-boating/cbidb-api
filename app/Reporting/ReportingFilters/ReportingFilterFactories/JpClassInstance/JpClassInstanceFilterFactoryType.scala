package Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance
import Entities.{JpClassInstance, JpClassType}
import Reporting.ReportingFilters._
import Services.PersistenceBroker

class JpClassInstanceFilterFactoryType extends ReportingFilterFactory[JpClassInstance] with ReportingFilterFactoryDropdown {
  val argTypes: List[ReportingFilterArgType] = List(ARG_DROPDOWN)
  val displayName: String = "By Class Type"
  val defaultValue: String = "1"
  def getFilter(pb: PersistenceBroker, arg: String): ReportingFilter[JpClassInstance] = new ReportingFilterFunction(pb, (_pb: PersistenceBroker) => {
    val typeId = arg.toInt
    implicit val pb: PersistenceBroker = _pb
    pb.getObjectsByFilters(
      JpClassInstance,
      List(JpClassInstance.fields.typeId.equalsConstant(typeId)),
      100
    ).toSet
  })

  def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]] = {
    val types: List[JpClassType] = pb.getAllObjectsOfClass(JpClassType)
    List(types.map(t => (t.values.typeId.get.toString, t.values.typeName.get)).sortWith((a, b) => a._2 < b._2))
  }
}
