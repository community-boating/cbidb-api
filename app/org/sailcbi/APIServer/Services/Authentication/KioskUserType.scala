package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PermissionsAuthority}

class KioskUserType(override val userName: String) extends NonMemberUserType(userName) {
	override def companion: UserTypeObject[KioskUserType] = KioskUserType
}

object KioskUserType extends UserTypeObject[KioskUserType] {
	val uniqueUserName = "KIOSK"

	override def create(userName: String): KioskUserType = new KioskUserType(userName)
	def create: KioskUserType = create(uniqueUserName)

	override def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String)(implicit PA: PermissionsAuthority): Option[String] =
		if (request.headers.get("Am-CBI-Kiosk").contains(kioskToken)) Some(uniqueUserName)
		else None

	override def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: UserType,
		requiredUserName: Option[String]
	): Option[String] = None
}