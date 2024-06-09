package org.sailcbi.APIServer.Entities.cacheable

import org.sailcbi.APIServer.Entities.cacheable.ApClassInstances.ApClassInstancesCacheKey
import org.sailcbi.APIServer.Entities.cacheable.DatetimeRange.DatetimeRangeCacheKey
import org.sailcbi.APIServer.Entities.cacheable.MembershipSales.MembershipSalesCacheKey
import org.sailcbi.APIServer.Entities.cacheable.sunset.SunsetCacheKey
import org.sailcbi.APIServer.Entities.cacheable.yearlydate.YearlyDateAndItemCacheKey

import java.time.format.DateTimeFormatter

object CacheKeys {
	// TODO: some kind of wrapper class that allows only one single read of the text (to ensure the same key is not reused)
	def apClassInstances(config: ApClassInstancesCacheKey): String = "ap-class-instances-" + config.date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
	def membershipSales(config: MembershipSalesCacheKey): String = s"membership-sales-${config.calendarYear}"
	def userPermissionsAuthority(config: String): String = s"user-permissions-authority-${config}"
	def signoutsToday: String = "signouts-today"
	def boatTypes = "boat-types"
	def ratings = "ratings"
	def accessState = "access-state"
	def accessProfiles = "access-profiles"
	def apClassTypes = "ap-class-types"
	def programTypes = "program-types"
	def apClassInstancesThisSeason = "apClassInstancesThisSeason"
	def sunset(config: SunsetCacheKey): String = s"sunset-${config.year}-${config.month}-${config.day.getOrElse("none")}"
	def datetimeRange(config: DatetimeRangeCacheKey): String = s"datetime-range-${config.startDate.format(DateTimeFormatter.ISO_DATE)}-${config.endDate.format(DateTimeFormatter.ISO_DATE)}-${config.rangeType}"
	def yearlyDateAndItem(config: YearlyDateAndItemCacheKey): String = s"yearly-date-${config.year}-${config.itemAlias}"
}
