package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{ProtoStorable, Filter, StorableObject}

class StringDatabaseField(entity: StorableObject[_], fieldName: String, fieldLength: Int) extends DatabaseField[String](entity, fieldName) {
  def getFieldLength: Int = fieldLength

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "varchar(" + getFieldLength + ")"
      case x if x == classOf[OracleBroker]  => "varchar2(" + getFieldLength + ")"
    }
  }

  def getValue(row: ProtoStorable): String = getOptionValue(row) match {
    case Some(x) => x
    case None => throw new Exception("Non-null field was null")
  }

  def getOptionValue(row: ProtoStorable): Option[String] = {
    row.stringFields.get(fieldName) match {
      case Some(Some(x)) => Some(x)
      case _ => None
    }
  }

  def equalsConstant(c: String): Filter  =
    Filter(getFullyQualifiedName + " = '" + c + "'")

  def getValueFromString(s: String): Option[String] = Some(s)
}