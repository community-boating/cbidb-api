package Storable.Fields.FieldValue

import Storable.Fields.StringDatabaseField
import Storable.StorableClass

class StringFieldValue(instance: StorableClass, field: StringDatabaseField) extends FieldValue[String](instance, field) {
  def getPersistenceLiteral: String = super.peek match {
    case Some("") => throw new Exception("Empty string not allowed in non-null string fields")
    case Some(x) => "'" + x + "'"
    case None => "NULL"
  }
}
