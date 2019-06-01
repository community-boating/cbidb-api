package Storable.Fields.FieldValue

import Storable.Fields.BooleanDatabaseField
import Storable.StorableClass

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField) extends FieldValue[Boolean](instance, field) {
	def getPersistenceLiteral: String =
		if (super.get) "'Y'" else "'N'"
}
