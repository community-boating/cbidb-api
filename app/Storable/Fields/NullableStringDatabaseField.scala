package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{DatabaseRow, Filter, StorableObject}

class NullableStringDatabaseField(entity: StorableObject[_], fieldName: String, fieldLength: Int) extends DatabaseField[Option[String]](entity, fieldName) {
  def getFieldLength: Int = fieldLength

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "varchar(" + getFieldLength + ")"
      case x if x == classOf[OracleBroker]  => "varchar2(" + getFieldLength + ")"
    }
  }

  def getValue(row: DatabaseRow): String = getOptionValue(row) match {
    case Some(x) => x
    case None => throw new Exception("Non-null field was null")
  }

  def getOptionValue(row: DatabaseRow): Option[String] = {
    row.stringFields.get(fieldName) match {
      case Some(Some(x)) => Some(x)
      case _ => None
    }
  }

  def equalsConstant(c: String): Filter  =
    Filter(getFullyQualifiedName + " = '" + c + "'")
}