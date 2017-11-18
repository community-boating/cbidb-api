package Services

abstract class ServerRunMode

object ServerRunMode {
  case object ROOT_MODE extends ServerRunMode
  case object MAINTENANCE_MODE extends ServerRunMode
  case object STAFF_MODE extends ServerRunMode
  case object MEMBER_MODE extends ServerRunMode
  case object PUBLIC_MODE extends ServerRunMode
}
