package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Promotion extends StorableClass(Promotion) {
	object values extends ValuesObject {
		val promoId = new IntFieldValue(self, Promotion.fields.promoId)
		val promoType = new NullableBooleanFIeldValue(self, Promotion.fields.promoType)
		val promoAmount = new NullableDoubleFieldValue(self, Promotion.fields.promoAmount)
		val redemptionCode = new NullableStringFieldValue(self, Promotion.fields.redemptionCode)
		val startDate = new NullableLocalDateTimeFieldValue(self, Promotion.fields.startDate)
		val endDate = new NullableLocalDateTimeFieldValue(self, Promotion.fields.endDate)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Promotion.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Promotion.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Promotion.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Promotion.fields.updatedBy)
	}
}

object Promotion extends StorableObject[Promotion] {
	val entityName: String = "PROMOTIONS"

	object fields extends FieldsObject {
		val promoId = new IntDatabaseField(self, "PROMO_ID")
		val promoType = new NullableBooleanDatabaseField(self, "PROMO_TYPE")
		val promoAmount = new NullableDoubleDatabaseField(self, "PROMO_AMOUNT")
		val redemptionCode = new NullableStringDatabaseField(self, "REDEMPTION_CODE", 100)
		val startDate = new NullableLocalDateTimeDatabaseField(self, "START_DATE")
		val endDate = new NullableLocalDateTimeDatabaseField(self, "END_DATE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.promoId
}