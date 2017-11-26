package Storable.Fields

import Services.PermissionsAuthority
import Services.PermissionsAuthority.{PERSISTENCE_SYSTEM_MYSQL, PERSISTENCE_SYSTEM_ORACLE}
import Storable.{Filter, ProtoStorable, StorableObject}

class NullableStringDatabaseField(entity: StorableObject[_], persistenceFieldName: String, fieldLength: Int) extends DatabaseField[Option[String]](entity, persistenceFieldName) {
  def getFieldLength: Int = fieldLength

  def getFieldType: String = getFieldLength match {
    case l if l == 1 => "char(" + getFieldLength + ")"
    case _ => PermissionsAuthority.getPersistenceSystem match {
      case PERSISTENCE_SYSTEM_MYSQL => "varchar(" + getFieldLength + ")"
      case PERSISTENCE_SYSTEM_ORACLE  => "varchar2(" + getFieldLength + ")"
    }
  }

  def findValueInProtoStorable(row: ProtoStorable): Option[Option[String]] = row.stringFields.get(this.getRuntimeFieldName)

  def equalsConstant(os: Option[String]): Filter = os match {
    case Some(s: String) => Filter(getFullyQualifiedName + " = '" + s + "'")
    case None => Filter(getFullyQualifiedName + " IS NULL")
  }

  def equalsConstantLowercase(os: Option[String]): Filter = os match {
    case Some(s: String) => Filter("lower(" + getFullyQualifiedName + ")" + " = '" + s.toLowerCase() + "'")
    case None => Filter(getFullyQualifiedName + " IS NULL")
  }

  def getValueFromString(s: String): Option[Option[String]] = if (s == "") Some(None) else Some(Some(s))
}