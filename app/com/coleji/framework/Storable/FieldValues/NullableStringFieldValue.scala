package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Storable.Fields.NullableStringDatabaseField
import com.coleji.framework.Storable.StorableClass

class NullableStringFieldValue(instance: StorableClass, field: NullableStringDatabaseField) extends FieldValue[Option[String]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.get match {
		case Some("") => ("NULL", List.empty)
		case Some(x) => ("?", List(x))
		case None => ("NULL", List.empty)
	}
}
