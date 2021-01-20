package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}

class RootUserType(override val userName: String) extends NonMemberUserType(userName) {
	override def companion: UserTypeObject[RootUserType] = RootUserType
}

object RootUserType extends UserTypeObject[RootUserType] {
	val uniqueUserName = "ROOT"

	override def create(userName: String): RootUserType = new RootUserType(userName)
	def create: RootUserType = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		if (
			request.headers.get(PermissionsAuthority.ROOT_AUTH_HEADER).contains("true") &&
					PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None
	}

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = None
}