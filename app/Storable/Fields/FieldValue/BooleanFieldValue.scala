package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.BooleanDatabaseField

case class BooleanFieldValue(field: BooleanDatabaseField, value: Boolean) extends FieldValue {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String =
    if (value) "'Y'" else "'N'"
}
