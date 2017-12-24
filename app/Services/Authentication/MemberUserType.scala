package Services.Authentication

import Services._
import play.api.mvc.{AnyContent, Request}

object MemberUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: Request[AnyContent], rootCB: CacheBroker): Option[String] = None

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None
}
