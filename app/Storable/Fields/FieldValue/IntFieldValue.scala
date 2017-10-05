package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.IntDatabaseField

class IntFieldValue(field: IntDatabaseField) extends FieldValue[Int] {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = value.get.toString
}
