package Storable.Fields.FieldValue

import Storable.Fields.DoubleDatabaseField

case class DoubleFieldValue(field: DoubleDatabaseField, value: Double) extends FieldValue {
  def getFieldName: String = field.getFieldName
  def getInsertValue: String = value.toString
}
