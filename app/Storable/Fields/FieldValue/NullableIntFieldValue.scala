package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.{IntDatabaseField, NullableIntDatabaseField}

class NullableIntFieldValue(field: NullableIntDatabaseField) extends FieldValue[Option[Int]](field) {
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String = value.get match {
    case Some(x) => x.toString
    case None => "NULL"
  }
}