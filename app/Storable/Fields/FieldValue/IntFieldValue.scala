package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.IntDatabaseField
import Storable.StorableClass

class IntFieldValue(instance: StorableClass, field: IntDatabaseField) extends FieldValue[Int](instance, field) {
  def getPersistenceLiteral(implicit pbClass: Class[_ <: PersistenceBroker]): String = super.get.toString
}
