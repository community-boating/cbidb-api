package org.sailcbi.APIServer.CbiUtil

import org.sailcbi.APIServer.Services.Authentication.{RootUserType, UserTypeObject}

object TestUserType {
	def apply(allowed: Set[UserTypeObject[_]], test: UserTypeObject[_]): Boolean =
		test.isInstanceOf[RootUserType] || allowed.contains(test)
}
