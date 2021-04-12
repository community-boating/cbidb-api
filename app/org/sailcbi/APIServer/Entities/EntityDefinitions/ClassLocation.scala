package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._

class ClassLocation extends StorableClass(ClassLocation) {
	object values extends ValuesObject {
		val locationId = new IntFieldValue(self, ClassLocation.fields.locationId)
		val locationName = new StringFieldValue(self, ClassLocation.fields.locationName)
	}
}

object ClassLocation extends StorableObject[ClassLocation] {
	val entityName: String = "CLASS_LOCATIONS"

	object fields extends FieldsObject {
		val locationId = new IntDatabaseField(self, "LOCATION_ID")
		val locationName = new StringDatabaseField(self, "LOCATION_NAME", 100)
	}

	def primaryKey: IntDatabaseField = fields.locationId
}