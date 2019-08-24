package org.sailcbi.APIServer.Storable.Fields.FieldValue

import org.sailcbi.APIServer.Storable.Fields.BooleanDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField) extends FieldValue[Boolean](instance, field) {
	def getPersistenceLiteral: String =
		if (super.get) "'Y'" else "'N'"
}
