package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{ProtoStorable, Filter, StorableObject}

class BooleanDatabaseField(entity: StorableObject[_], fieldName: String) extends DatabaseField[Boolean](entity, fieldName) {
  def getFieldLength: Int = 1

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "char(1)"
      case x if x == classOf[OracleBroker]  => "char(1)"
    }
  }

  def getValue(row: ProtoStorable): Boolean = getOptionValue(row) match {
    case Some(x) => x
    case None => throw new Exception("Non-null field was null")
  }

  def getOptionValue(row: ProtoStorable): Option[Boolean] = {
    row.stringFields.get(fieldName) match {
      case Some(Some("Y")) => Some(true)
      case Some(Some("N")) => Some(false)
      case Some(None) => Some(false)
      case _ => None
    }
  }

  def equals(b: Boolean): Filter  =
    Filter(getFullyQualifiedName + " = '" + (if (b) "Y" else "N") + "'")
}