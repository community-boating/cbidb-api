package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Storable.Fields.BooleanDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField) extends FieldValue[Boolean](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val value = if (super.get) "'Y'" else "'N'"
		(value, List.empty)
	}
}
