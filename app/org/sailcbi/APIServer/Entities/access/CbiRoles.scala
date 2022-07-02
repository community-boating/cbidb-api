package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.Role

object CbiRoles {
	val ROLE_GENERAL_ADMIN = Role(512, "General Admin", "Access to misc management screens/functionality", List(
		CbiPermissions.PERM_GENERAL_ADMIN
	))
	val ROLE_DOCKMASTER = Role(513, "Dockmaster", List(
		CbiPermissions.PERM_SIGN_DOCK_REPORT,
		CbiPermissions.PERM_UPDATE_DOCK_REPORT_RESTRICTED
	))
}
