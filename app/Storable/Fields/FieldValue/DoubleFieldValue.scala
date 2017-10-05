package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.DoubleDatabaseField

class DoubleFieldValue(field: DoubleDatabaseField) extends FieldValue[Double] {
  def getFieldName: String = field.getFieldName
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = value.get.toString
}
