package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{BooleanFieldValue, IntFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{BooleanDatabaseField, IntDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable._

class ClassLocation extends StorableClass(ClassLocation) {
	object values extends ValuesObject {
		val locationId = new IntFieldValue(self, ClassLocation.fields.locationId)
		val locationName = new StringFieldValue(self, ClassLocation.fields.locationName)
		val active = new BooleanFieldValue(self, ClassLocation.fields.active)
	}
}

object ClassLocation extends StorableObject[ClassLocation] {
	val entityName: String = "CLASS_LOCATIONS"

	object fields extends FieldsObject {
		val locationId = new IntDatabaseField(self, "LOCATION_ID")
		val locationName = new StringDatabaseField(self, "LOCATION_NAME", 100)
		val active = new BooleanDatabaseField(self, "ACTIVE", true)
	}

	def primaryKey: IntDatabaseField = fields.locationId
}