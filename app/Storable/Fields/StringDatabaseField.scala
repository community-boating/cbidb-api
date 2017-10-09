package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{Filter, ProtoStorable, StorableObject}

class StringDatabaseField(entity: StorableObject[_], persistenceFieldName: String, fieldLength: Int) extends DatabaseField[String](entity, persistenceFieldName) {
  def getFieldLength: Int = fieldLength

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "varchar(" + getFieldLength + ")"
      case x if x == classOf[OracleBroker]  => "varchar2(" + getFieldLength + ")"
    }
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[String] = {
    row.stringFields.get(this.getRuntimeFieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => throw new Exception("non-null String field " + entity.entityName + "." + this.getRuntimeFieldName + " was null in a proto")
      case _ => None
    }
  }

  def equalsConstant(c: String): Filter  =
    Filter(getFullyQualifiedName + " = '" + c + "'")

  def getValueFromString(s: String): Option[String] = Some(s)
}