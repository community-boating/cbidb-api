package Services

class PermissionsAuthority(val rm: ServerRunMode, val pb: PersistenceBroker, val cb: CacheBroker) {
  println("Spawning new PermissionsAuthority")
}
