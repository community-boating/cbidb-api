package org.sailcbi.APIServer.Reporting.ReportingFilters

import org.sailcbi.APIServer.Services.PersistenceBroker
import org.sailcbi.APIServer.Storable.StorableClass

class ReportingFilterFunction[T <: StorableClass](pb: PersistenceBroker, fn: (PersistenceBroker => Set[T])) extends ReportingFilter[T] {
	lazy val instances: Set[T] = fn(pb)
}