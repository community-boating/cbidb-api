package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class BoatRating extends StorableClass(BoatRating) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, BoatRating.fields.assignId)
		val boatId = new IntFieldValue(self, BoatRating.fields.boatId)
		val programId = new IntFieldValue(self, BoatRating.fields.programId)
		val flag = new StringFieldValue(self, BoatRating.fields.flag)
		val ratingId = new IntFieldValue(self, BoatRating.fields.ratingId)
		val createdOn = new DateTimeFieldValue(self, BoatRating.fields.createdOn)
		val createdBy = new StringFieldValue(self, BoatRating.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, BoatRating.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, BoatRating.fields.updatedBy)
	}
}

object BoatRating extends StorableObject[BoatRating] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "BOATS_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val flag = new StringDatabaseField(self, "FLAG", 1)
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}