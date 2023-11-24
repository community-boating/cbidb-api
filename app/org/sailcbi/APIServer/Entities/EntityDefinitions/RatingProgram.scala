package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class RatingProgram extends StorableClass(RatingProgram) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, RatingProgram.fields.assignId)
		val ratingId = new IntFieldValue(self, RatingProgram.fields.ratingId)
		val programId = new IntFieldValue(self, RatingProgram.fields.programId)
		val createdOn = new DateTimeFieldValue(self, RatingProgram.fields.createdOn)
		val createdBy = new StringFieldValue(self, RatingProgram.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, RatingProgram.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, RatingProgram.fields.updatedBy)
	}
}

object RatingProgram extends StorableObject[RatingProgram] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "RATINGS_PROGRAMS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		@NullableInDatabase
		val ratingId = new IntDatabaseField(self, "RATING_ID")
		@NullableInDatabase
		val programId = new IntDatabaseField(self, "PROGRAM_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.assignId
}