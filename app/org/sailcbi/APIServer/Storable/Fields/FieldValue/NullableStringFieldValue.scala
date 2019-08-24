package org.sailcbi.APIServer.Storable.Fields.FieldValue

import org.sailcbi.APIServer.Storable.Fields.NullableStringDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class NullableStringFieldValue(instance: StorableClass, field: NullableStringDatabaseField) extends FieldValue[Option[String]](instance, field) {
	def getPersistenceLiteral: String = super.get match {
		case Some("") => "NULL"
		case Some(x) => "'" + x + "'"
		case None => "NULL"
	}
}
