package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.StorableObject

class DoubleDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField(entity, fieldName) {
  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "decimal"
    case x if x == classOf[OracleBroker] => "number"
  }
}