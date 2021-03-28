package org.sailcbi.APIServer.Reporting.ReportingFilters

import org.sailcbi.APIServer.Services.{RequestCache, UnlockedRequestCache}
import org.sailcbi.APIServer.Storable.StorableClass

class ReportingFilterFunction[T <: StorableClass](rc: UnlockedRequestCache, fn: UnlockedRequestCache => Set[T]) extends ReportingFilter[T] {
	lazy val instances: Set[T] = fn(rc)
}