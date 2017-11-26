package Storable.Fields.FieldValue

import Storable.Fields.IntDatabaseField
import Storable.StorableClass

class IntFieldValue(instance: StorableClass, field: IntDatabaseField) extends FieldValue[Int](instance, field) {
  def getPersistenceLiteral: String = super.get.toString
}
