package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonRating extends StorableClass(PersonRating) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
		val rating = new Initializable[Rating]
		val program = new Initializable[ProgramType]
	}

	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonRating.fields.assignId)
		val personId = new IntFieldValue(self, PersonRating.fields.personId)
		val ratingId = new IntFieldValue(self, PersonRating.fields.ratingId)
		val createdOn = new NullableDateTimeFieldValue(self, PersonRating.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonRating.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, PersonRating.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonRating.fields.updatedBy)
		val programId = new IntFieldValue(self, PersonRating.fields.programId)
		val testSignoutId = new NullableIntFieldValue(self, PersonRating.fields.testSignoutId)
	}
}

object PersonRating extends StorableObject[PersonRating] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val testSignoutId = new NullableIntDatabaseField(self, "TEST_SIGNOUT_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}