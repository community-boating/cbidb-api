package org.sailcbi.APIServer.Entities.cacheable

import org.sailcbi.APIServer.Entities.cacheable.ApClassInstances.ApClassInstancesCacheKey
import org.sailcbi.APIServer.Entities.cacheable.MembershipSales.MembershipSalesCacheKey
import org.sailcbi.APIServer.Entities.cacheable.sunset.SunsetCacheKey

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
	def sunset(config: SunsetCacheKey): String = s"sunset-${config.year}-${config.month}"
}
