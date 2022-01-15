package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class RatingsProgram extends StorableClass(RatingsProgram) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, RatingsProgram.fields.assignId)
		val ratingId = new NullableIntFieldValue(self, RatingsProgram.fields.ratingId)
		val programId = new NullableIntFieldValue(self, RatingsProgram.fields.programId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, RatingsProgram.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, RatingsProgram.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, RatingsProgram.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, RatingsProgram.fields.updatedBy)
	}
}

object RatingsProgram extends StorableObject[RatingsProgram] {
	val entityName: String = "RATINGS_PROGRAMS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val ratingId = new NullableIntDatabaseField(self, "RATING_ID")
		val programId = new NullableIntDatabaseField(self, "PROGRAM_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}