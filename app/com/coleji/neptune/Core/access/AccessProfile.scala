package com.coleji.neptune.Core.access

case class AccessProfile(
	id: Int,
	name: String,
	roles: Set[Role]
)

object AccessProfile {
	def apply(id: Int, name: String): AccessProfile = AccessProfile(id, name, Set.empty)
	// start app-specific profiles at 64
}
