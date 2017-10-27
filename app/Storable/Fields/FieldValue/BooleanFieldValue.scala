package Storable.Fields.FieldValue

import Services.PersistenceBroker
import Storable.Fields.BooleanDatabaseField
import Storable.StorableClass

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField) extends FieldValue[Boolean](instance, field) {
  def getPersistenceLiteral(implicit pb: PersistenceBroker): String =
    if (super.get) "'Y'" else "'N'"
}
