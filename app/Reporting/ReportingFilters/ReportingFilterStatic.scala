package Reporting.ReportingFilters

import Storable.StorableClass

class ReportingFilterStatic[T <: StorableClass](val instances: Set[T]) extends ReportingFilter[T]