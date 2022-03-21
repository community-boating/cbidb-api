package com.coleji.neptune.Core.access

class UserPermissionsAuthority[T_User](
	val user: T_User,
	val roles: Set[Role],
	val permissions: Set[Permission]
) {
	lazy val permissionList: List[Int] = permissions.toList.map(_.id)
	/**
	 * @return true if you can reset permissions at all, and the other user doesnt have any permissions that you dont
	 */
	def canResetPasswordUser(otherUser: UserPermissionsAuthority[T_User]): Boolean = {
		permissions.contains(Permission.PERM_RESET_PASSWORD) && otherUser.permissions.diff(permissions).isEmpty
	}
}

