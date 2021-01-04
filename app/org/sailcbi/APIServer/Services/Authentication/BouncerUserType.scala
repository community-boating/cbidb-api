package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}

class BouncerUserType(override val userName: String) extends NonMemberUserType(userName) {
	override def companion: UserTypeObject[BouncerUserType] = BouncerUserType
}

object BouncerUserType extends UserTypeObject[BouncerUserType] {
	val uniqueUserName = "BOUNCER"

	override def create(userName: String): BouncerUserType = new BouncerUserType(userName)
	def create: BouncerUserType = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		apexToken: String,
		kioskToken: String
	)(implicit PA: PermissionsAuthority): Option[String] =
		if (
			request.headers.get(PermissionsAuthority.BOUNCER_AUTH_HEADER).contains("true") &&
					PA.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootUserType]) Some(RootUserType.uniqueUserName) else None
}