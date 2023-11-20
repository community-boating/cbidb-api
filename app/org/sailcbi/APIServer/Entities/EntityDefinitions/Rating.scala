package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Rating extends StorableClass(Rating) {
	override object references extends ReferencesObject {
		val boats = new Initializable[IndexedSeq[BoatRating]]
		val programs = new Initializable[IndexedSeq[RatingProgram]]
	}

	override object values extends ValuesObject {
		val ratingId = new IntFieldValue(self, Rating.fields.ratingId)
		val ratingName = new StringFieldValue(self, Rating.fields.ratingName)
		val createdOn = new DateTimeFieldValue(self, Rating.fields.createdOn)
		val createdBy = new StringFieldValue(self, Rating.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Rating.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, Rating.fields.updatedBy)
		val displayOrder = new DoubleFieldValue(self, Rating.fields.displayOrder)
		val active = new NullableBooleanFieldValue(self, Rating.fields.active)
		val overriddenBy = new NullableDoubleFieldValue(self, Rating.fields.overriddenBy)
		val testMinCrew = new NullableDoubleFieldValue(self, Rating.fields.testMinCrew)
		val testMaxCrew = new NullableDoubleFieldValue(self, Rating.fields.testMaxCrew)
		val testable = new NullableBooleanFieldValue(self, Rating.fields.testable)
		val ratingCategory = new StringFieldValue(self, Rating.fields.ratingCategory)
	}
}

object Rating extends StorableObject[Rating] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "RATINGS"

	object fields extends FieldsObject {
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val ratingName = new StringDatabaseField(self, "RATING_NAME", 100)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val overriddenBy = new NullableDoubleDatabaseField(self, "OVERRIDDEN_BY")
		val testMinCrew = new NullableDoubleDatabaseField(self, "TEST_MIN_CREW")
		val testMaxCrew = new NullableDoubleDatabaseField(self, "TEST_MAX_CREW")
		val testable = new NullableBooleanDatabaseField(self, "TESTABLE")
		val ratingCategory = new StringDatabaseField(self, "RATING_CATEGORY", 1)
	}

	def primaryKey: IntDatabaseField = fields.ratingId
}