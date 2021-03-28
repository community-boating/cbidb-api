package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Storable.Fields.NullableDoubleDatabaseField
import com.coleji.framework.Storable.StorableClass

class NullableDoubleFieldValue(instance: StorableClass, field: NullableDoubleDatabaseField) extends FieldValue[Option[Double]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.get match {
		case Some(x) => (x.toString, List.empty)
		case None => ("NULL", List.empty)
	}
}