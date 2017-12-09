package Services

import Entities.Rating
import play.api.mvc.Request

class RequestCache private[Services] (
  val authenticatedUserName: String,
  val request: Request[_],
  val pb: PersistenceBroker,
  val cb: CacheBroker
) {
  println("Spawning new RequestCache")
  // TODO: some way to confirm that things like this have no security on them (regardless of if we pass or fail in this req)
  // TODO: dont do this every request.
  lazy val ratings: Set[Rating] = pb.getAllObjectsOfClass(Rating).toSet
}
