package com.coleji.framework.Export

import org.sailcbi.APIServer.Services.UnlockedRequestCache

trait ReportingFilterFactoryDropdown {
	// outer list is per dropdown (e.g. a filter that renders two dropdowns)
	// inner list is values within that dropdown
	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]]
}
