package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Storable.Fields.NullableIntDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class NullableIntFieldValue(instance: StorableClass, field: NullableIntDatabaseField) extends FieldValue[Option[Int]](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = super.get match {
		case Some(x) => (x.toString, List.empty)
		case None => ("NULL", List.empty)
	}
}