package Reporting.ReportingFilters

import Services.PersistenceBroker
import Storable.StorableClass

class ReportingFilterFunction[T <: StorableClass](pb: PersistenceBroker, fn: (PersistenceBroker => Set[T])) extends ReportingFilter[T] {
  lazy val instances: Set[T] = fn(pb)
}