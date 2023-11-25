package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class ClassLocation extends StorableClass(ClassLocation) {
	override object values extends ValuesObject {
		val locationId = new IntFieldValue(self, ClassLocation.fields.locationId)
		val locationName = new StringFieldValue(self, ClassLocation.fields.locationName)
		val active = new BooleanFieldValue(self, ClassLocation.fields.active)
		val createdOn = new NullableDateTimeFieldValue(self, ClassLocation.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ClassLocation.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, ClassLocation.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ClassLocation.fields.updatedBy)
	}
}

object ClassLocation extends StorableObject[ClassLocation] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CLASS_LOCATIONS"

	object fields extends FieldsObject {
		val locationId = new IntDatabaseField(self, "LOCATION_ID")
		@NullableInDatabase
		val locationName = new StringDatabaseField(self, "LOCATION_NAME", 100)
		@NullableInDatabase
		val active = new BooleanDatabaseField(self, "ACTIVE", false)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.locationId
}