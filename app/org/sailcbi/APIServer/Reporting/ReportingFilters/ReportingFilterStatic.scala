package org.sailcbi.APIServer.Reporting.ReportingFilters

import com.coleji.framework.Storable.StorableClass

class ReportingFilterStatic[T <: StorableClass](val instances: Set[T]) extends ReportingFilter[T]