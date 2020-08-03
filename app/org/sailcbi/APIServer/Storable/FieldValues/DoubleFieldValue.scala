package org.sailcbi.APIServer.Storable.FieldValues

import org.sailcbi.APIServer.Storable.Fields.DoubleDatabaseField
import org.sailcbi.APIServer.Storable.StorableClass

class DoubleFieldValue(instance: StorableClass, field: DoubleDatabaseField) extends FieldValue[Double](instance, field) {
	def getPersistenceLiteral: (String, List[String]) = (super.get.toString, List.empty)
}