package Reporting.ReportingFilters.ReportingFilterFactories

import Reporting.ReportingFilters.ReportingFilter
import Storable.StorableClass

abstract class ReportingFilterFactory[T <: StorableClass] {
  def getFilter(args: String): ReportingFilter[T]
}
