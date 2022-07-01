package org.sailcbi.APIServer.Entities.access

import com.coleji.neptune.Core.access.Permission

object CbiPermissions {
	val PERM_GENERAL_ADMIN = Permission(1024, "General Admin", "Access to misc management screens/functionality")
	val PERM_SIGN_DOCK_REPORT = Permission(1025, "Sign Dock report")
	val PERM_UPDATE_DOCK_REPORT_RESTRICTED = Permission(1026, "Update restricted Dock Report fields")
}
