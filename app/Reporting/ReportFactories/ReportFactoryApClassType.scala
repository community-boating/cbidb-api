package Reporting.ReportFactories

import Entities.{ApClassInstance, ApClassType, JpClassType}
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Reporting.{ReportFactory, ReportingField}
import Storable.StorableObject

class ReportFactoryApClassType extends ReportFactory[ApClassType] {
  def decorateInstancesWithParentReferences(instances: List[ApClassType]): Unit = {} // no parents!

  val entityCompanion: StorableObject[ApClassType] = ApClassType

  val FIELD_MAP: Map[String, ReportingField[ApClassType]] = Map(
    "TypeId" -> ReportingField.getReportingFieldFromDatabaseField(ApClassType.fields.typeId, "Type ID"),
    "TypeName" -> ReportingField.getReportingFieldFromDatabaseField(ApClassType.fields.typeName, "Type Name")
  )

  val FILTER_MAP: Map[String, ReportingFilterFactory[ApClassType, _]] = Map()
}
