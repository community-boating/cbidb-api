package Services.Authentication

import Services.{CacheBroker, PermissionsAuthority, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{AnyContent, Request}

object ApexUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: Request[AnyContent], rootCB: CacheBroker): Option[String] = {
    val headers = request.headers.toMap
    val headerKey = "apex-token"
    if (headers.contains(headerKey) && headers(headerKey).mkString("") == PermissionsAuthority.apexToken.get) Some("APEX")
    else None
  }

  def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

  def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
