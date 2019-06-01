package Services.Authentication

import CbiUtil.ParsedRequest
import Services.{CacheBroker, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}

abstract class UserType {
	// Given a request (and an unrestricted CacheBroker), determine if the request is authenticated against this mechanism.
	// Return Some(authenticated username) if so, None otherwise
	def getAuthenticatedUsernameInRequest(
												 request: ParsedRequest,
												 rootCB: CacheBroker,
												 apexToken: String,
												 kioskToken: String
										 ): Option[String]

	// If the request actually came from e.g. a Staff request, but we want to access a Member or Public endpoint,
	// use this to downgrade the request authentication
	final def getAuthFromSuperiorAuth(
											 currentAuthentication: AuthenticationInstance,
											 requiredUserName: Option[String]
									 ): Option[AuthenticationInstance] = getAuthenticatedUsernameFromSuperiorAuth(currentAuthentication, requiredUserName) match {
		case Some(userName) => Some(AuthenticationInstance(this, userName))
		case None => None
	}

	protected def getAuthenticatedUsernameFromSuperiorAuth(
																  currentAuthentication: AuthenticationInstance,
																  requiredUserName: Option[String]
														  ): Option[String]

	// Given a username (and an unrestricted PersistenceBroker), get the (hashingGeneration, psHash) that is active for the user
	def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)]

	def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility
}
