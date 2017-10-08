package Storable.Fields

import Services.{MysqlBroker, OracleBroker, PersistenceBroker}
import Storable.{ProtoStorable, Filter, StorableObject}

class NullableStringDatabaseField(entity: StorableObject[_], persistenceFieldName: String, fieldLength: Int) extends DatabaseField[Option[String]](entity, persistenceFieldName) {
  def getFieldLength: Int = fieldLength

  def getFieldType(implicit pbClass: Class[_ <: PersistenceBroker]): String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => pbClass match {
      case x if x == classOf[MysqlBroker] => "varchar(" + getFieldLength + ")"
      case x if x == classOf[OracleBroker]  => "varchar2(" + getFieldLength + ")"
    }
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[Option[String]] = row.stringFields.get(this.getRuntimeFieldName)

  def equalsConstant(os: Option[String]): Filter = os match {
    case Some(s: String) => Filter(getFullyQualifiedName + " = '" + s + "'")
    case None => Filter(getFullyQualifiedName + " IS NULL")
  }

  def getValueFromString(s: String): Option[Option[String]] = if (s == "") Some(None) else Some(Some(s))
}