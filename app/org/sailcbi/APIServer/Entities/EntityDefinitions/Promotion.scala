package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class Promotion extends StorableClass(Promotion) {
	override object values extends ValuesObject {
		val promoId = new IntFieldValue(self, Promotion.fields.promoId)
		val promoType = new NullableStringFieldValue(self, Promotion.fields.promoType)
		val promoAmount = new NullableDoubleFieldValue(self, Promotion.fields.promoAmount)
		val redemptionCode = new NullableStringFieldValue(self, Promotion.fields.redemptionCode)
		val startDate = new NullableDateTimeFieldValue(self, Promotion.fields.startDate)
		val endDate = new NullableDateTimeFieldValue(self, Promotion.fields.endDate)
		val createdOn = new DateTimeFieldValue(self, Promotion.fields.createdOn)
		val createdBy = new StringFieldValue(self, Promotion.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Promotion.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, Promotion.fields.updatedBy)
	}
}

object Promotion extends StorableObject[Promotion] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PROMOTIONS"

	object fields extends FieldsObject {
		val promoId = new IntDatabaseField(self, "PROMO_ID")
		val promoType = new NullableStringDatabaseField(self, "PROMO_TYPE", 1)
		val promoAmount = new NullableDoubleDatabaseField(self, "PROMO_AMOUNT")
		val redemptionCode = new NullableStringDatabaseField(self, "REDEMPTION_CODE", 100)
		val startDate = new NullableDateTimeDatabaseField(self, "START_DATE")
		val endDate = new NullableDateTimeDatabaseField(self, "END_DATE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.promoId
}