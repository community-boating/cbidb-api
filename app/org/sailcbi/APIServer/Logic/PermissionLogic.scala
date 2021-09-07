package org.sailcbi.APIServer.Logic

import org.sailcbi.APIServer.Entities.EntityDefinitions.User

object PermissionLogic {
	def canAccessUserAdmin(u: User): Boolean = Set(User.USER_TYPES.MANAGER, User.USER_TYPES.ADMIN) contains u.values.userType.get
}
