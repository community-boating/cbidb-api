package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.NullableStringDatabaseField
import Storable.StorableClass

class NullableStringFieldValue(instance: StorableClass, field: NullableStringDatabaseField) extends FieldValue[Option[String]](instance, field) {
  def getPersistenceLiteral(implicit pbClass: Class[_ <: PersistenceBroker]): String = super.get match {
    case Some("") => "NULL"
    case Some(x) => "'" + x + "'"
    case None => "NULL"
  }
}
