package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Storable.Fields.IntDatabaseField
import com.coleji.framework.Storable.StorableClass

class IntFieldValue(instance: StorableClass, field: IntDatabaseField) extends FieldValue[Int](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = (super.get.toString, List.empty)
}
