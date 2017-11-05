package Reporting.ReportFactories

import java.time.format.DateTimeFormatter

import Entities.{JpClassInstance, JpClassType}
import Reporting.{ReportFactory, ReportingField}
import Reporting.ReportingFilters.ReportingFilterFactories.JpClassInstance.{JpClassInstanceFilterFactoryType, JpClassInstanceFilterFactoryYear}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Storable.StorableObject

class ReportFactoryJpClassType extends ReportFactory[JpClassType] {
  def decorateInstancesWithParentReferences(instances: List[JpClassType]): Unit = {} // no parents!

  val entityCompanion: StorableObject[JpClassType] = JpClassType

  val FIELD_MAP: Map[String, ReportingField[JpClassType]] = Map(
    "TypeId" -> ReportingField.getReportingFieldFromDatabaseField(JpClassType.fields.typeId, "Type ID"),
    "TypeName" -> ReportingField.getReportingFieldFromDatabaseField(JpClassType.fields.typeName, "Type Name")
  )

  val FILTER_MAP: Map[String, ReportingFilterFactory[JpClassType, _]] = Map()
}
