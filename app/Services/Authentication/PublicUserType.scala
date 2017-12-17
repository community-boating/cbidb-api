package Services.Authentication

import Services._
import play.api.mvc.{AnyContent, Request}

object PublicUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: Request[AnyContent], rootCB: CacheBroker): Option[String] = None

  def getPwHashForUser(request: Request[AnyContent], userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def spawnRequestCache(request: Request[AnyContent], rootCB: CacheBroker): RequestCache =
    new RequestCache("", request, getPB, new RedisBroker())
}
