package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.StorableObject

class DateTimeDatabaseField(entity: StorableObject[_], fieldName: String) extends DateDatabaseField(entity, fieldName) {
  override def getFieldType(pbClass: Class[_ <: PersistenceBroker]): String = pbClass match {
    case x if x == classOf[MysqlBroker] => "datetime"
    case x if x == classOf[OracleBroker] => "date"
  }
}