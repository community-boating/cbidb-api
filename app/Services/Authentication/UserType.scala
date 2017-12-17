package Services.Authentication

import Services.{CacheBroker, OracleBroker, PersistenceBroker, RequestCache}
import play.api.mvc.{AnyContent, Request}

abstract class UserType {
  // Given a request (and an unrestricted CacheBroker), determine if the request is authenticated against this mechanism.
  // Return Some(authenticated username) if so, None otherwise
  def getAuthenticatedUsernameInRequest(request: Request[AnyContent], rootCB: CacheBroker): Option[String]

  // Given a username (and an unrestricted PersistenceBroker), get the (hashingGeneration, psHash) that is active for the user
  def getPwHashForUser(request: Request[AnyContent], userName: String, rootPB: PersistenceBroker): Option[(Int, String)]

  def spawnRequestCache(request: Request[AnyContent], rootCB: CacheBroker): RequestCache

  protected def getPB: PersistenceBroker = new OracleBroker(this)
}