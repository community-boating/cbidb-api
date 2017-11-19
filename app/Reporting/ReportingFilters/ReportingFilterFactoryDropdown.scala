package Reporting.ReportingFilters

import Services.PersistenceBroker

trait ReportingFilterFactoryDropdown {
  def getDropdownValues(pb: PersistenceBroker): List[(String, String)]
}