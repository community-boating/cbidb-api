package ScalaTest

import Services.{OracleConnectionPoolConstructor, PermissionsAuthority, PersistenceBroker, RelationalBroker}

object GetPersistenceBroker {
  def apply(block: PersistenceBroker => Unit): Unit = {
    println("************  SPINNING UP  ************")
    val poolConstructor = new OracleConnectionPoolConstructor
    RelationalBroker.initialize(poolConstructor, () => {})
    PermissionsAuthority.instanceName.set(poolConstructor.getMainSchemaName)
    try {
      block(PermissionsAuthority.getRootPB.get)
    } finally {
      println("************  SHUTTING DOWN  ************")
      RelationalBroker.shutdown()
    }
  }
}
