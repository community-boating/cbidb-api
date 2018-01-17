package Services.Authentication

import Services.{CacheBroker, PersistenceBroker}
import Storable.{EntityVisibility, StorableClass, StorableObject}
import play.api.mvc.{AnyContent, Request}

object ApexUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: Request[AnyContent], rootCB: CacheBroker): Option[String] = {
    println("$$$$$$$$$$$$$$$$$$    APEX USER AUTHENTICATION IS HARDCODED ALWAYS ON!!!!   $$$$$$$$$$$$$$$$$$$$$$$$$$")
    Some("APEX2")
  }

def getPwHashForUser(userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = None

def getEntityVisibility(obj: StorableObject[_ <: StorableClass]): EntityVisibility = EntityVisibility.ZERO_VISIBILITY
}
