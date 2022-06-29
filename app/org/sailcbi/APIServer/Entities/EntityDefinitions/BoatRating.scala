package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class BoatRating extends StorableClass(BoatRating) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, BoatRating.fields.assignId)
		val boatId = new IntFieldValue(self, BoatRating.fields.boatId)
		val programId = new IntFieldValue(self, BoatRating.fields.programId)
		val flag = new StringFieldValue(self, BoatRating.fields.flag)
		val ratingId = new IntFieldValue(self, BoatRating.fields.ratingId)
	}
}

object BoatRating extends StorableObject[BoatRating] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	val entityName: String = "BOATS_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		val flag = new StringDatabaseField(self, "FLAG", 1)
		val ratingId = new IntDatabaseField(self, "RATING_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}