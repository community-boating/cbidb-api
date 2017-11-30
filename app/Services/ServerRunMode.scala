package Services

abstract class ServerRunMode

// Original design also included MAINTENANCE_MODE (vs STAFF_MODE) and MEMBER_MODE/PUBLIC_MODE
// Now thinking there will only really be three server instance types:
// Someone's PC (ROOT_MODE)
// The boathouse (STAFF_MODE)
// A cloud server (INTERNET_MODE)
// Requests should declare the minimum mode in which they can be called,
// and then we need another layer of security based on requesting user
object ServerRunMode {
  case object ROOT_MODE extends ServerRunMode
  case object STAFF_MODE extends ServerRunMode
  case object INTERNET_MODE extends ServerRunMode
}
