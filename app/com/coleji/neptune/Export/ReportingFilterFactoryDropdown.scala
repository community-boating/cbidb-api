package com.coleji.neptune.Export

import com.coleji.neptune.Core.UnlockedRequestCache

trait ReportingFilterFactoryDropdown {
	// outer list is per dropdown (e.g. a filter that renders two dropdowns)
	// inner list is values within that dropdown
	def getDropdownValues(rc: UnlockedRequestCache): List[List[(String, String)]]
}
