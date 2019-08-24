package org.sailcbi.APIServer.Reporting.ReportingFilters

import org.sailcbi.APIServer.Storable.StorableClass

class ReportingFilterStatic[T <: StorableClass](val instances: Set[T]) extends ReportingFilter[T]