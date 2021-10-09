package com.coleji.neptune.Export

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.StorableClass

class ReportingFilterFunction[T <: StorableClass](rc: UnlockedRequestCache, fn: UnlockedRequestCache => Set[T]) extends ReportingFilter[T] {
	lazy val instances: Set[T] = fn(rc)
}