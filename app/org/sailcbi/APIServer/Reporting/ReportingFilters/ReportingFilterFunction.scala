package org.sailcbi.APIServer.Reporting.ReportingFilters

import com.coleji.framework.Storable.StorableClass
import org.sailcbi.APIServer.Services.{RequestCache, UnlockedRequestCache}

class ReportingFilterFunction[T <: StorableClass](rc: UnlockedRequestCache, fn: UnlockedRequestCache => Set[T]) extends ReportingFilter[T] {
	lazy val instances: Set[T] = fn(rc)
}