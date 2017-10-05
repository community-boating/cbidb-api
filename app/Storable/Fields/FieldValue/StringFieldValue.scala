package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.StringDatabaseField

class StringFieldValue(field: StringDatabaseField) extends FieldValue[String] {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = "'" + value.get + "'"
}
