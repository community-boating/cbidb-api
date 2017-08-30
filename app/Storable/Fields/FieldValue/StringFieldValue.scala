package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.StringDatabaseField

case class StringFieldValue(field: StringDatabaseField, value: String) extends FieldValue {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = "'" + value + "'"
}
