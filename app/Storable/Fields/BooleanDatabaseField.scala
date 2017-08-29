package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, StorableObject}

class BooleanDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField[Boolean](entity, fieldName) {
  def getFieldLength: Int = 1

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "char(1)"
      case x if x == classOf[OracleBroker]  => "char(1)"
    }
  }

  def equals(b: Boolean): Filter  =
    Filter(getFullyQualifiedName + " = '" + (if (b) "Y" else "N") + "'")
}