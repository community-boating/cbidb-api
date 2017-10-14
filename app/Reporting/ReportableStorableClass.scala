package Reporting

import Reporting.ReportingFilters.ReportingFilter
import Storable.StorableClass

trait ReportableStorableClass extends StorableClass {
  val ReportingFilterSubclass: Class[_ <: ReportingFilter[_]]
}
