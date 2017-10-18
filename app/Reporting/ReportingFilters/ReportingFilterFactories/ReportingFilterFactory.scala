package Reporting.ReportingFilters.ReportingFilterFactories

import Reporting.ReportingFilters.ReportingFilter
import Services.PersistenceBroker
import Storable.StorableClass

abstract class ReportingFilterFactory[T <: StorableClass] {
  val displayName: String
  def getFilter(pb: PersistenceBroker, args: String): ReportingFilter[T]
}
