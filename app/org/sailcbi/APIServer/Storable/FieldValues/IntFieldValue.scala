package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Storable.Fields.IntDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class IntFieldValue(instance: StorableClass, field: IntDatabaseField) extends FieldValue[Int](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = (super.get.toString, List.empty)
}
