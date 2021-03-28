package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Storable.Fields.NullableIntDatabaseField
import com.coleji.framework.Storable.StorableClass

class NullableIntFieldValue(instance: StorableClass, field: NullableIntDatabaseField) extends FieldValue[Option[Int]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.get match {
		case Some(x) => (x.toString, List.empty)
		case None => ("NULL", List.empty)
	}
}