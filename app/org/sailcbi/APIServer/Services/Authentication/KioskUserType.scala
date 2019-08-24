package org.sailcbi.APIServer.Services.Authentication

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.Services.{CacheBroker, PersistenceBroker}
import org.sailcbi.APIServer.Storable.{EntityVisibility, StorableClass, StorableObject}

object KioskUserType extends NonMemberUserType {
	val uniqueUserName = "KIOSK"

	def getAuthenticatedUsernameInRequest(request: ParsedRequest, rootCB: CacheBroker, apexToken: String, kioskToken: String): Option[String] =
		if (request.headers.get("Am-CBI-Kiosk").contains(kioskToken)) Some(uniqueUserName)
		else None

	def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: AuthenticationInstance,
		requiredUserName: Option[String]
	): Option[String] = None

	def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

	def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
