package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpRatingsGranted extends StorableClass(JpRatingsGranted) {
	object values extends ValuesObject {
		val grantId = new IntFieldValue(self, JpRatingsGranted.fields.grantId)
		val ratingId = new NullableIntFieldValue(self, JpRatingsGranted.fields.ratingId)
		val quantity = new NullableDoubleFieldValue(self, JpRatingsGranted.fields.quantity)
		val drDate = new NullableLocalDateTimeFieldValue(self, JpRatingsGranted.fields.drDate)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpRatingsGranted.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpRatingsGranted.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpRatingsGranted.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpRatingsGranted.fields.updatedBy)
	}
}

object JpRatingsGranted extends StorableObject[JpRatingsGranted] {
	val entityName: String = "JP_RATINGS_GRANTED"

	object fields extends FieldsObject {
		val grantId = new IntDatabaseField(self, "GRANT_ID")
		val ratingId = new NullableIntDatabaseField(self, "RATING_ID")
		val quantity = new NullableDoubleDatabaseField(self, "QUANTITY")
		val drDate = new NullableLocalDateTimeDatabaseField(self, "DR_DATE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.grantId
}