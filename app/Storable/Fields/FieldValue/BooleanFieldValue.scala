package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.BooleanDatabaseField

class BooleanFieldValue(field: BooleanDatabaseField) extends FieldValue[Boolean] {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String =
    if (value.get) "'Y'" else "'N'"
}
