package com.coleji.neptune.Export

import com.coleji.neptune.Storable.StorableClass

class ReportingFilterStatic[T <: StorableClass](val instances: Set[T]) extends ReportingFilter[T]