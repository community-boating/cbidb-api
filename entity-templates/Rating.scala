package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Rating extends StorableClass(Rating) {
	object values extends ValuesObject {
		val ratingId = new IntFieldValue(self, Rating.fields.ratingId)
		val ratingName = new NullableStringFieldValue(self, Rating.fields.ratingName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Rating.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Rating.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Rating.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Rating.fields.updatedBy)
		val displayOrder = new NullableDoubleFieldValue(self, Rating.fields.displayOrder)
		val active = new NullableBooleanFIeldValue(self, Rating.fields.active)
		val overriddenBy = new NullableDoubleFieldValue(self, Rating.fields.overriddenBy)
		val testMinCrew = new NullableDoubleFieldValue(self, Rating.fields.testMinCrew)
		val testMaxCrew = new NullableDoubleFieldValue(self, Rating.fields.testMaxCrew)
		val testable = new NullableBooleanFIeldValue(self, Rating.fields.testable)
	}
}

object Rating extends StorableObject[Rating] {
	val entityName: String = "RATINGS"

	object fields extends FieldsObject {
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val ratingName = new NullableStringDatabaseField(self, "RATING_NAME", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val overriddenBy = new NullableDoubleDatabaseField(self, "OVERRIDDEN_BY")
		val testMinCrew = new NullableDoubleDatabaseField(self, "TEST_MIN_CREW")
		val testMaxCrew = new NullableDoubleDatabaseField(self, "TEST_MAX_CREW")
		val testable = new NullableBooleanDatabaseField(self, "TESTABLE")
	}

	def primaryKey: IntDatabaseField = fields.ratingId
}