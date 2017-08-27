package Storable.Fields.FieldValue

import Services.PersistenceBroker

abstract class FieldValue {
  def getFieldName: String
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String
}
