package org.sailcbi.APIServer.Reporting.ReportingFilters

import org.sailcbi.APIServer.Services.RequestCache
import org.sailcbi.APIServer.Storable.StorableClass

class ReportingFilterFunction[T <: StorableClass](rc: RequestCache, fn: RequestCache => Set[T]) extends ReportingFilter[T] {
	lazy val instances: Set[T] = fn(rc)
}