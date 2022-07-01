package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.AccessProfile

object CbiAccessProfiles {
	val DEV_OPS = AccessProfile(64, "DevOps")
	val GLOBAL_ADMIN = AccessProfile(65, "Global Admin")
	val JP_DIRECTOR = AccessProfile(66, "JP Director")
	val AP_DIRECTOR = AccessProfile(67, "AP Director")
	val FO_MANAGER = AccessProfile(68, "FO Manager")
	val FO_SUPERVISOR = AccessProfile(69, "FO Supervisor")
	val FO_STAFF = AccessProfile(70, "FO Staff")
	val DOCKMASTER = AccessProfile(71, "Dockmaster")
	val DOCKSTAFF = AccessProfile(72, "Dockstaff")
	val ACCOUNTANT = AccessProfile(73, "Accountant")
	val JP_INSTRUCTOR = AccessProfile(74, "JP Instructor")
	val GUEST = AccessProfile(75, "Guest")
}
