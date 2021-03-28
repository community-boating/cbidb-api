package com.coleji.framework.Storable.FieldValues

import com.coleji.framework.Storable.Fields.StringDatabaseField
import com.coleji.framework.Storable.StorableClass

class StringFieldValue(instance: StorableClass, field: StringDatabaseField) extends FieldValue[String](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.peek match {
		case Some("") => throw new Exception("Empty string not allowed in non-null string fields")
		case Some(x) => ("?", List(x))
		case None => ("NULL", List.empty)
	}
}
