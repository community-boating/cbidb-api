package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class RatingChange extends StorableClass(RatingChange) {
	object values extends ValuesObject {
		val changeId = new IntFieldValue(self, RatingChange.fields.changeId)
		val personId = new IntFieldValue(self, RatingChange.fields.personId)
		val ratingId = new IntFieldValue(self, RatingChange.fields.ratingId)
		val programId = new IntFieldValue(self, RatingChange.fields.programId)
		val action = new StringFieldValue(self, RatingChange.fields.action)
		val changedBy = new NullableStringFieldValue(self, RatingChange.fields.changedBy)
		val changedDate = new DateTimeFieldValue(self, RatingChange.fields.changedDate)
		val comments = new NullableStringFieldValue(self, RatingChange.fields.comments)
	}
}

object RatingChange extends StorableObject[RatingChange] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	val entityName: String = "RATING_CHANGES"

	object fields extends FieldsObject {
		val changeId = new IntDatabaseField(self, "CHANGE_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val action = new StringDatabaseField(self, "ACTION", 1)
		val changedBy = new NullableStringDatabaseField(self, "CHANGED_BY", 50)
		val changedDate = new DateTimeDatabaseField(self, "CHANGED_DATE")
		val comments = new NullableStringDatabaseField(self, "COMMENTS", 4000)
	}

	def primaryKey: IntDatabaseField = fields.changeId
}