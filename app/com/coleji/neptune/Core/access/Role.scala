package com.coleji.neptune.Core.access

case class Role(
	id: Int,
	name: String,
	description: String,
	permissions: Set[Permission]
)

object Role {
	def apply(id: Int, name: String, description: String): Role = Role(id, name, description, Set.empty)

	val ROLE_DATA_ADMINISTRATOR = Role(1, "Data Adminstrator", "Super admin with all the low level access")
	val ROLE_GLOBAL_USER_ADMIN = Role(2, "Global User Admin", "Unlimited access to manage users")

	// start app-specific roles at 512
}