package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.BooleanDatabaseField

class BooleanFieldValue(field: BooleanDatabaseField) extends FieldValue[Boolean](field) {
  def getInsertValue(implicit pbClass: Class[_ <: PersistenceBroker]): String =
    if (super.get) "'Y'" else "'N'"
}
