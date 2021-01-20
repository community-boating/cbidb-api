package org.sailcbi.APIServer.Reporting.ReportingFilters

import org.sailcbi.APIServer.Services.RequestCache

trait ReportingFilterFactoryDropdown {
	// outer list is per dropdown (e.g. a filter that renders two dropdowns)
	// inner list is values within that dropdown
	def getDropdownValues(rc: RequestCache[_]): List[List[(String, String)]]
}