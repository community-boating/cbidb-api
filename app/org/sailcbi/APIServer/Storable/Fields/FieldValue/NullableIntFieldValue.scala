package org.sailcbi.APIServer.Storable.Fields.FieldValue

import org.sailcbi.APIServer.Storable.Fields.NullableIntDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class NullableIntFieldValue(instance: StorableClass, field: NullableIntDatabaseField) extends FieldValue[Option[Int]](instance, field) {
	def getPersistenceLiteral: String = super.get match {
		case Some(x) => x.toString
		case None => "NULL"
	}
}