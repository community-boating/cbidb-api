package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsRating extends StorableClass(PersonsRating) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonsRating.fields.assignId)
		val personId = new IntFieldValue(self, PersonsRating.fields.personId)
		val ratingId = new IntFieldValue(self, PersonsRating.fields.ratingId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsRating.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsRating.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonsRating.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsRating.fields.updatedBy)
		val programId = new IntFieldValue(self, PersonsRating.fields.programId)
		val testSignoutId = new NullableIntFieldValue(self, PersonsRating.fields.testSignoutId)
	}
}

object PersonsRating extends StorableObject[PersonsRating] {
	val entityName: String = "PERSONS_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val testSignoutId = new NullableIntDatabaseField(self, "TEST_SIGNOUT_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}