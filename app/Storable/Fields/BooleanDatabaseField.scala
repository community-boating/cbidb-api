package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class BooleanDatabaseField(entity: StorableObject[_], persistenceFieldName: String, nullImpliesFalse: Boolean = false) extends DatabaseField[Boolean](entity, persistenceFieldName) {
  def getFieldLength: Int = 1

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "char(1)"
      case x if x == classOf[OracleBroker]  => "char(1)"
    }
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[Boolean] = {
    row.stringFields.get(this.getRuntimeFieldName) match {
      case Some(Some("Y")) => Some(true)
      case Some(Some("N")) => Some(false)
      case Some(None) =>
        if (nullImpliesFalse) Some(false)
        else throw new Exception("non-null Boolean field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def equals(b: Boolean): Filter  =
    Filter(getFullyQualifiedName + " = '" + (if (b) "Y" else "N") + "'")

  def getValueFromString(s: String): Option[Boolean] = s.toLowerCase match {
    case "true" => Some(true)
    case "false" => Some(false)
    case _ => None
  }
}