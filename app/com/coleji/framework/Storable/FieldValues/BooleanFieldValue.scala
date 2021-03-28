package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Storable.Fields.BooleanDatabaseField
import com.coleji.framework.Storable.StorableClass

class BooleanFieldValue(instance: StorableClass, field: BooleanDatabaseField) extends FieldValue[Boolean](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = {
		val value = if (super.get) "'Y'" else "'N'"
		(value, List.empty)
	}
}
