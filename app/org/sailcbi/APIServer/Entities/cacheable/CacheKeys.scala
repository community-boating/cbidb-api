package org.sailcbi.APIServer.Entities.cacheable

import org.sailcbi.APIServer.Entities.cacheable.MembershipSales.MembershipSalesCacheKey

object CacheKeys {
	def membershipSales(config: MembershipSalesCacheKey) = s"membership-sales-${config.calendarYear}"
}
