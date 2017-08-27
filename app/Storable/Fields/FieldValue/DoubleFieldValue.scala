package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.DoubleDatabaseField

case class DoubleFieldValue(field: DoubleDatabaseField, value: Double) extends FieldValue {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = value.toString
}
