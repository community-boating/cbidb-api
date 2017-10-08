package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.StringDatabaseField

class StringFieldValue(field: StringDatabaseField) extends FieldValue[String](field) {
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = "'" + super.get + "'"
}
