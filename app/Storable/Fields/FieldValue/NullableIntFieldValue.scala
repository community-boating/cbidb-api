package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.NullableIntDatabaseField
import Storable.StorableClass

class NullableIntFieldValue(instance: StorableClass, field: NullableIntDatabaseField) extends FieldValue[Option[Int]](instance, field) {
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = super.get match {
    case Some(x) => x.toString
    case None => "NULL"
  }
}