package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class BoatType extends StorableClass(BoatType) {
	object values extends ValuesObject {
		val boatId = new IntFieldValue(self, BoatType.fields.boatId)
		val boatName = new NullableStringFieldValue(self, BoatType.fields.boatName)
		val active = new NullableBooleanFIeldValue(self, BoatType.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, BoatType.fields.displayOrder)
		val createdOn = new NullableLocalDateTimeFieldValue(self, BoatType.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, BoatType.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, BoatType.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, BoatType.fields.updatedBy)
		val minCrew = new NullableDoubleFieldValue(self, BoatType.fields.minCrew)
		val maxCrew = new NullableDoubleFieldValue(self, BoatType.fields.maxCrew)
		val imageFilename = new NullableStringFieldValue(self, BoatType.fields.imageFilename)
	}
}

object BoatType extends StorableObject[BoatType] {
	val entityName: String = "BOAT_TYPES"

	object fields extends FieldsObject {
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val boatName = new NullableStringDatabaseField(self, "BOAT_NAME", 50)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val minCrew = new NullableDoubleDatabaseField(self, "MIN_CREW")
		val maxCrew = new NullableDoubleDatabaseField(self, "MAX_CREW")
		val imageFilename = new NullableStringDatabaseField(self, "IMAGE_FILENAME", 50)
	}

	def primaryKey: IntDatabaseField = fields.boatId
}