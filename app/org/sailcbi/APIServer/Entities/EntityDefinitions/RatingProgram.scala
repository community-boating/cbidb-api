package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class RatingProgram extends StorableClass(RatingProgram) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, RatingProgram.fields.assignId)
		val ratingId = new IntFieldValue(self, RatingProgram.fields.ratingId)
		val programId = new IntFieldValue(self, RatingProgram.fields.programId)
	}
}

object RatingProgram extends StorableObject[RatingProgram] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	val entityName: String = "RATINGS_PROGRAMS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}