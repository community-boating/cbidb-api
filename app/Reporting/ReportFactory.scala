package Reporting

import Reporting.ReportingFields.ReportingField
import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterSpecParser}
import Services.PersistenceBroker
import Storable.StorableClass

abstract class ReportFactory[T <: StorableClass](pb: PersistenceBroker, filterSpec: String, fieldSpec: String) {
  type ValueFunction = (T => String)

  val FIELD_MAP: Map[String, ReportingField[T]]

  val FILTER_MAP: Map[String, ReportingFilterFactory[T]]

  def getReportText: String = {
    val fields = getFields
    val filter = getCombinedFilter
    val instances = filter.instances.asInstanceOf[Set[T]].toList
    decorateInstancesWithParentReferences(instances)
    val valueFunctions: List[ValueFunction] = fields.map(f => f.getValueFunction(pb, instances))

    fields.map(f => f.fieldDisplayName).mkString("\t") +
      "\n" +
      instances.map(i => {
        valueFunctions.map(fn => {
          fn(i)
        }).mkString("\t")
      }).mkString("\n")
  }

  protected def decorateInstancesWithParentReferences(instances: List[T]): Unit

  private def getCombinedFilter: ReportingFilter[T] = {
    val parser: ReportingFilterSpecParser[T] = new ReportingFilterSpecParser[T](pb, FILTER_MAP)
    parser.parse(filterSpec)
  }

  def getFields: List[ReportingField[T]] = {
    val FIELD_SEPARATOR: Char = ','
    fieldSpec.split(FIELD_SEPARATOR).toList.map(FIELD_MAP(_))
  }
}
