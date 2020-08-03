package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Storable.Fields.NullableStringDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class NullableStringFieldValue(instance: StorableClass, field: NullableStringDatabaseField) extends FieldValue[Option[String]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.get match {
		case Some("") => ("NULL", List.empty)
		case Some(x) => ("?", List(x))
		case None => ("NULL", List.empty)
	}
}
