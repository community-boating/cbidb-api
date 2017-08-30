package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.StringDatabaseField

case class NullableStringFieldValue(field: StringDatabaseField, value: Option[String]) extends FieldValue {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = value match {
    case Some("") => throw new Exception("Empty string not allowed in non-null string fields")
    case Some(x) => "'" + x + "'"
    case None => "NULL"
  }
}
