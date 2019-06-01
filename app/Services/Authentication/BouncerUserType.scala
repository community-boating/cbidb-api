package Services.Authentication

import CbiUtil.ParsedRequest
import Entities.EntityDefinitions.User
import Services.{CacheBroker, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}

object BouncerUserType extends UserType {
	val uniqueUserName = ""

	def getAuthenticatedUsernameInRequest(
												 request: ParsedRequest,
												 rootCB: CacheBroker,
												 apexToken: String,
												 kioskToken: String
										 ): Option[String] = None

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
