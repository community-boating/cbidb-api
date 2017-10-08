package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.StringDatabaseField
import Storable.StorableClass

class StringFieldValue(instance: StorableClass, field: StringDatabaseField) extends FieldValue[String](instance, field) {
  def getPersistenceLiteral(implicit pbClass: Class[_ <: PersistenceBroker]): String = "'" + super.get + "'"
}
