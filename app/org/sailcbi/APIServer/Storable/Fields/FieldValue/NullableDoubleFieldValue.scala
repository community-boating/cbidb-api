package org.sailcbi.APIServer.Storable.Fields.FieldValue

import org.sailcbi.APIServer.Storable.Fields.NullableDoubleDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class NullableDoubleFieldValue(instance: StorableClass, field: NullableDoubleDatabaseField) extends FieldValue[Option[Double]](instance, field) {
	def getPersistenceLiteral: String = super.get match {
		case Some(x) => x.toString
		case None => "NULL"
	}
}