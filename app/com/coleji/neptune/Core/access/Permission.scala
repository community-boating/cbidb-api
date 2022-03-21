package com.coleji.neptune.Core.access

case class Permission(
	id: Int,
	name: String,
	description: String
)

object Permission {
	val PERM_RESET_PASSWORD = Permission(1, "Reset Password", "Reset passwords for users, as long as they don't have more access (can't reset upward)")
	val PERM_CAN_GRANT_ADHOC = Permission(2, "Can grant adhoc", "Users with this permission can grant other permissions they have to other users, on a time-restricted basis")
	
	// App permissions should start at 1024
}