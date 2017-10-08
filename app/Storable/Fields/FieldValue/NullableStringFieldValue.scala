package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.NullableStringDatabaseField

class NullableStringFieldValue(field: NullableStringDatabaseField) extends FieldValue[Option[String]](field) {
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = super.get match {
    case Some("") => throw new Exception("Empty string not allowed in non-null string fields")
    case Some(x) => "'" + x + "'"
    case None => "NULL"
  }
}
