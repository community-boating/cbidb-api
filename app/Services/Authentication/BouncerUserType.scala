package Services.Authentication

import CbiUtil.ParsedRequest
import Entities.EntityDefinitions.User
import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}

object BouncerUserType extends NonMemberUserType {
	val uniqueUserName = "BOUNCER"

	def getAuthenticatedUsernameInRequest(
		request: ParsedRequest,
		rootCB: CacheBroker,
		apexToken: String,
		kioskToken: String
	): Option[String] =
		if (
			request.headers.get(PermissionsAuthority.BOUNCER_AUTH_HEADER).contains("true") &&
					PermissionsAuthority.requestIsFromLocalHost(request)
		) Some(uniqueUserName)
		else None

	def getAuthenticatedUsernameFromSuperiorAuth(
		currentAuthentication: AuthenticationInstance,
		requiredUserName: Option[String]
	): Option[String] = if (currentAuthentication.userType == RootUserType) Some(RootUserType.uniqueUserName) else None

	def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

	def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = obj match {
		case User => EntityVisibility(true, None, Some(Set(
			User.fields.userId,
			User.fields.pwHash
		)))
		case _ => EntityVisibility.ZERO_VISIBILITY
	}
}
