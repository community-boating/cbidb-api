package Services

import Entities.Rating
import Services.Authentication.{PublicUserType, RootUserType, UserType}
import play.api.mvc.{AnyContent, Request}

// TODO: Some sort of security on the CacheBroker so arbitrary requests can't see the authentication tokens
// TODO: mirror all PB methods on RC so the RC can either pull from redis or dispatch to oracle etc
class RequestCache private[RequestCache] (
  val authenticatedUserName: String,
  val authenticatedUserType: UserType
) {
  val pb: PersistenceBroker = new OracleBroker(this)
  val cb: CacheBroker = new RedisBroker
  println("Spawning new RequestCache")
  // TODO: some way to confirm that things like this have no security on them (regardless of if we pass or fail in this req)
  // TODO: dont do this every request.
  lazy val ratings: Set[Rating] = pb.getAllObjectsOfClass(Rating).toSet
}

object RequestCache {
  // TODO: better way to handle requests authenticated against multiple mechanisms?
  def construct(request: Request[AnyContent], rootCB: CacheBroker): RequestCache = {
    // For all the enabled user types (besides public), see if the request is authenticated against any of them.
    val ret: Option[RequestCache] = PermissionsAuthority.allowableUserTypes.get
      .filter(_ != PublicUserType)
      .foldLeft(None: Option[RequestCache])((retInner: Option[RequestCache], ut: UserType) => retInner match {
        case Some(x) => Some(x)
        case None => ut.getAuthenticatedUsernameInRequest(request, rootCB) match {
          case None => None
          case Some(x: String) => Some(new RequestCache(x, ut))
        }
      })

    ret match {
      case Some(x) => x
      case None => new RequestCache("", PublicUserType)
    }
  }

   lazy private[Services] val getRootRC: RequestCache = new RequestCache("", RootUserType)
}