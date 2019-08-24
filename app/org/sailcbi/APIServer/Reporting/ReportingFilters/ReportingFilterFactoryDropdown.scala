package org.sailcbi.APIServer.Reporting.ReportingFilters

import org.sailcbi.APIServer.Services.PersistenceBroker

trait ReportingFilterFactoryDropdown {
	// outer list is per dropdown (e.g. a filter that renders two dropdowns)
	// inner list is values within that dropdown
	def getDropdownValues(pb: PersistenceBroker): List[List[(String, String)]]
}