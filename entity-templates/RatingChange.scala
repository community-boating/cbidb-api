package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class RatingChange extends StorableClass(RatingChange) {
	object values extends ValuesObject {
		val changeId = new IntFieldValue(self, RatingChange.fields.changeId)
		val personId = new IntFieldValue(self, RatingChange.fields.personId)
		val ratingId = new IntFieldValue(self, RatingChange.fields.ratingId)
		val programId = new IntFieldValue(self, RatingChange.fields.programId)
		val action = new NullableBooleanFIeldValue(self, RatingChange.fields.action)
		val changedBy = new NullableStringFieldValue(self, RatingChange.fields.changedBy)
		val changedDate = new NullableLocalDateTimeFieldValue(self, RatingChange.fields.changedDate)
		val comments = new NullableStringFieldValue(self, RatingChange.fields.comments)
		val createdOn = new NullableLocalDateTimeFieldValue(self, RatingChange.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, RatingChange.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, RatingChange.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, RatingChange.fields.updatedBy)
	}
}

object RatingChange extends StorableObject[RatingChange] {
	val entityName: String = "RATING_CHANGES"

	object fields extends FieldsObject {
		val changeId = new IntDatabaseField(self, "CHANGE_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val action = new NullableBooleanDatabaseField(self, "ACTION")
		val changedBy = new NullableStringDatabaseField(self, "CHANGED_BY", 50)
		val changedDate = new NullableLocalDateTimeDatabaseField(self, "CHANGED_DATE")
		val comments = new NullableStringDatabaseField(self, "COMMENTS", 4000)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.changeId
}