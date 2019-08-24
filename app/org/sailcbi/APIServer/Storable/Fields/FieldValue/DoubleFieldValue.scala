package org.sailcbi.APIServer.Storable.Fields.FieldValue

import org.sailcbi.APIServer.Storable.Fields.DoubleDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class DoubleFieldValue(instance: StorableClass, field: DoubleDatabaseField) extends FieldValue[Double](instance, field) {
	def getPersistenceLiteral: String = super.get.toString
}