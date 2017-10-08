package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.IntDatabaseField

class IntFieldValue(field: IntDatabaseField) extends FieldValue[Int](field) {
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = super.get.toString
}
