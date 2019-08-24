package org.sailcbi.APIServer.Storable.Fields.FieldValue

import org.sailcbi.APIServer.Storable.Fields.IntDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class IntFieldValue(instance: StorableClass, field: IntDatabaseField) extends FieldValue[Int](instance, field) {
	def getPersistenceLiteral: String = super.get.toString
}
