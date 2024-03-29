package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class MemTypesAllowedRating extends StorableClass(MemTypesAllowedRating) {
	override object values extends ValuesObject {
		val assignId = new IntFieldValue(self, MemTypesAllowedRating.fields.assignId)
		val membershipTypeId = new IntFieldValue(self, MemTypesAllowedRating.fields.membershipTypeId)
		val ratingId = new IntFieldValue(self, MemTypesAllowedRating.fields.ratingId)
	}
}

object MemTypesAllowedRating extends StorableObject[MemTypesAllowedRating] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "MEM_TYPES_ALLOWED_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		@NullableInDatabase
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		@NullableInDatabase
		val ratingId = new IntDatabaseField(self, "RATING_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}