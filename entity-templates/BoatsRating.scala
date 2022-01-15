package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class BoatsRating extends StorableClass(BoatsRating) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, BoatsRating.fields.assignId)
		val boatId = new NullableIntFieldValue(self, BoatsRating.fields.boatId)
		val programId = new NullableIntFieldValue(self, BoatsRating.fields.programId)
		val flag = new NullableBooleanFIeldValue(self, BoatsRating.fields.flag)
		val ratingId = new NullableIntFieldValue(self, BoatsRating.fields.ratingId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, BoatsRating.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, BoatsRating.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, BoatsRating.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, BoatsRating.fields.updatedBy)
	}
}

object BoatsRating extends StorableObject[BoatsRating] {
	val entityName: String = "BOATS_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val boatId = new NullableIntDatabaseField(self, "BOAT_ID")
		val programId = new NullableIntDatabaseField(self, "PROGRAM_ID")
		val flag = new NullableBooleanDatabaseField(self, "FLAG")
		val ratingId = new NullableIntDatabaseField(self, "RATING_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}