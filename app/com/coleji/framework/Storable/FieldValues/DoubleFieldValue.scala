package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Storable.Fields.DoubleDatabaseField
import com.coleji.framework.Storable.StorableClass

class DoubleFieldValue(instance: StorableClass, field: DoubleDatabaseField) extends FieldValue[Double](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = (super.get.toString, List.empty)
}