package Services

import CbiUtil.Initializable
import play.api.mvc.Request

class PermissionsAuthority (val rm: ServerRunMode) {
  println("############## SETTING RUN MODE ############")
  PermissionsAuthority.rm.set(rm)
}

object PermissionsAuthority {
  val rm = new Initializable[ServerRunMode]
  def spawnRequestCache(request: Request[_]): RequestCache = {
    val pb = new OracleBroker
    val cb = new RedisBroker
    new RequestCache(request, pb, cb)
  }
}