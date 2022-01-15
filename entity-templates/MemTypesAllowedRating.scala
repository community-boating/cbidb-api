package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class MemTypesAllowedRating extends StorableClass(MemTypesAllowedRating) {
	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, MemTypesAllowedRating.fields.assignId)
		val membershipTypeId = new NullableIntFieldValue(self, MemTypesAllowedRating.fields.membershipTypeId)
		val ratingId = new NullableIntFieldValue(self, MemTypesAllowedRating.fields.ratingId)
	}
}

object MemTypesAllowedRating extends StorableObject[MemTypesAllowedRating] {
	val entityName: String = "MEM_TYPES_ALLOWED_RATINGS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val ratingId = new NullableIntDatabaseField(self, "RATING_ID")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}