package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}

class ApexUserType(override val userName: String) extends NonMemberUserType(userName) {
	override def companion: UserTypeObject[ApexUserType] = ApexUserType
}

object ApexUserType extends UserTypeObject[ApexUserType] {
	val uniqueUserName = "APEX"

	override def create(userName: String): ApexUserType = new ApexUserType(userName)
	def create: ApexUserType = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] = {
		val headers = request.headers.toMap
		val headerKey = "apex-token"
		if (headers.contains(headerKey) && headers(headerKey).mkString("") == apexToken) Some(uniqueUserName)
		else {
			// signet?
			val signetKey = "apex-signet"
			if (
				headers.contains(signetKey) && PA.validateApexSignet(Some(headers(signetKey).mkString("")))
			) Some(uniqueUserName)
			else None
		}
	}

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.isInstanceOf[RootUserType]) Some(RootUserType.uniqueUserName) else None
}