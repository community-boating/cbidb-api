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

  def getValue(row: DatabaseRow): Option[String] = {
    row.stringFields.get(fieldName) match {
      case Some(Some(x)) => Some(x)
      case Some(None) => None
      case None => throw new Exception("Nullable String did not exist in DatabaseRow: " + fieldName)
    }
  }

  def equalsConstant(os: Option[String]): Filter = os match {
    case Some(s: String) => Filter(getFullyQualifiedName + " = '" + s + "'")
    case None => Filter(getFullyQualifiedName + " IS NULL")
  }
}