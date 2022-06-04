package org.sailcbi.APIServer.Entities.cacheable

import org.sailcbi.APIServer.Entities.cacheable.ApClassInstances.ApClassInstancesCacheKey
import org.sailcbi.APIServer.Entities.cacheable.MembershipSales.MembershipSalesCacheKey

import java.time.format.DateTimeFormatter

object CacheKeys {
	def apClassInstances(config: ApClassInstancesCacheKey): String = "ap-class-instances-" + config.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
	def membershipSales(config: MembershipSalesCacheKey): String = s"membership-sales-${config.calendarYear}"
	def userPermissionsAuthority(config: String): String = s"user-permissions-authority-${config}"
}
