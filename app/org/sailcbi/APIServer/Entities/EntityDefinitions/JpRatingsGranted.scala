package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpRatingsGranted extends StorableClass(JpRatingsGranted) {
	override object values extends ValuesObject {
		val grantId = new IntFieldValue(self, JpRatingsGranted.fields.grantId)
		val ratingId = new NullableIntFieldValue(self, JpRatingsGranted.fields.ratingId)
		val quantity = new NullableDoubleFieldValue(self, JpRatingsGranted.fields.quantity)
		val drDate = new NullableDateTimeFieldValue(self, JpRatingsGranted.fields.drDate)
		val createdOn = new DateTimeFieldValue(self, JpRatingsGranted.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpRatingsGranted.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpRatingsGranted.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpRatingsGranted.fields.updatedBy)
	}
}

object JpRatingsGranted extends StorableObject[JpRatingsGranted] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_RATINGS_GRANTED"

	object fields extends FieldsObject {
		val grantId = new IntDatabaseField(self, "GRANT_ID")
		val ratingId = new NullableIntDatabaseField(self, "RATING_ID")
		val quantity = new NullableDoubleDatabaseField(self, "QUANTITY")
		val drDate = new NullableDateTimeDatabaseField(self, "DR_DATE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.grantId
}