package com.coleji.neptune.Core.access

case class Title (
	id: Int,
	name: String,
	reportsTo: Option[Title],
	roles: Set[Role]
)

object Title {
	def apply(id: Int, name: String, reportsTo: Title, roles: Set[Role]): Title = Title(id, name, Some(reportsTo), roles)
	def apply(id: Int, name: String, roles: Set[Role]): Title = Title(id, name, None, roles)
	// start app-specific titles at 64
}
