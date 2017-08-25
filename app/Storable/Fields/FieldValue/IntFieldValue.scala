package Storable.Fields.FieldValue

import Storable.Fields.IntDatabaseField

case class IntFieldValue(field: IntDatabaseField, value: Int) extends FieldValue {
  def getFieldName: String = field.getFieldName
  def getInsertValue: String = value.toString
}
