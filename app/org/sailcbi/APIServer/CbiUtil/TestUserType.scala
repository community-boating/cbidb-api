package org.sailcbi.APIServer.CbiUtil

import org.sailcbi.APIServer.Services.Authentication.{RootUserType, UserType}

object TestUserType {
	def apply(allowed: Set[UserType], test: UserType): Boolean =
		test == RootUserType || allowed.contains(test)
}
