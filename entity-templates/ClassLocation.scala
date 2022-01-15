package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ClassLocation extends StorableClass(ClassLocation) {
	object values extends ValuesObject {
		val locationId = new IntFieldValue(self, ClassLocation.fields.locationId)
		val locationName = new NullableStringFieldValue(self, ClassLocation.fields.locationName)
		val active = new NullableBooleanFIeldValue(self, ClassLocation.fields.active)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ClassLocation.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ClassLocation.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ClassLocation.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ClassLocation.fields.updatedBy)
	}
}

object ClassLocation extends StorableObject[ClassLocation] {
	val entityName: String = "CLASS_LOCATIONS"

	object fields extends FieldsObject {
		val locationId = new IntDatabaseField(self, "LOCATION_ID")
		val locationName = new NullableStringDatabaseField(self, "LOCATION_NAME", 100)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.locationId
}