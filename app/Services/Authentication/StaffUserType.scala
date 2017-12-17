package Services.Authentication

import Entities.User
import Services.PermissionsAuthority.UnauthorizedAccessException
import Services._
import play.api.mvc.{AnyContent, Request}

object StaffUserType extends UserType {
  def getAuthenticatedUsernameInRequest(request: Request[AnyContent], rootCB: CacheBroker): Option[String] = {
    println(request.cookies)
    val secCookies = request.cookies.filter(_.name == PermissionsAuthority.SEC_COOKIE_NAME)
    if (secCookies.isEmpty) None
    else if (secCookies.size > 1) None
    else {
      val cookie = secCookies.toList.head
      val token = cookie.value
      println(rootCB.get("dfkjdgfjkdgfjkdgf"))
      val cacheResult = rootCB.get(PermissionsAuthority.SEC_COOKIE_NAME + "_" + token)
      println(cacheResult)
      cacheResult match {
        case None => None
        case Some(s: String) => {
          val split = s.split(",")
          if (split.length != 2) None
          val userName = split(0)
          val expires = split(1)
          println("expires ")
          println(expires)
          println("and its currently ")
          println(System.currentTimeMillis())
          if (expires.toLong < System.currentTimeMillis()) None
          else Some(userName)
        }
      }
    }
  }

  def getPwHashForUser(request: Request[AnyContent], userName: String, rootPB: PersistenceBroker): Option[(Int, String)] = {
    val users = rootPB.getObjectsByFilters(
      User,
      List(User.fields.userName.equalsConstantLowercase(userName))
    )

    if (users.length == 1) Some(1, users.head.values.pwHash.get)
    else None
  }

  def spawnRequestCache(request: Request[AnyContent], rootCB: CacheBroker): RequestCache = {
    val authenticatedUserName = getAuthenticatedUsernameInRequest(request, rootCB)
    println("AUTHENTICATED: " + authenticatedUserName)
    println("Prod mode? " + PermissionsAuthority.serverRunMode)
    if (authenticatedUserName.isDefined) {
      val pb = getPB
      val cb = new RedisBroker
      val userName = if(authenticatedUserName.isDefined) authenticatedUserName.get else ""
      new RequestCache(userName, request, pb, cb)
    } else {
      throw new UnauthorizedAccessException
    }
  }
}
