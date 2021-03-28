package com.coleji.framework.Export

import com.coleji.framework.Core.UnlockedRequestCache
import com.coleji.framework.Storable.StorableClass

class ReportingFilterFunction[T <: StorableClass](rc: UnlockedRequestCache, fn: UnlockedRequestCache => Set[T]) extends ReportingFilter[T] {
	lazy val instances: Set[T] = fn(rc)
}