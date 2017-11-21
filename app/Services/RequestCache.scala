package Services

import CbiUtil.SelfInitializable
import Entities.Rating

class RequestCache(implicit pb: PersistenceBroker) {
  println("Spawning new RequestCache")
  val ratings: SelfInitializable[Set[Rating]] = new SelfInitializable[Set[Rating]](() => {
    pb.getAllObjectsOfClass(Rating).toSet
  })


}
