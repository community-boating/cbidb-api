package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DonationSuggestion extends StorableClass(DonationSuggestion) {
	object values extends ValuesObject {
		val suggId = new IntFieldValue(self, DonationSuggestion.fields.suggId)
		val amount = new NullableDoubleFieldValue(self, DonationSuggestion.fields.amount)
		val createdOn = new NullableLocalDateTimeFieldValue(self, DonationSuggestion.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DonationSuggestion.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, DonationSuggestion.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DonationSuggestion.fields.updatedBy)
	}
}

object DonationSuggestion extends StorableObject[DonationSuggestion] {
	val entityName: String = "DONATION_SUGGESTIONS"

	object fields extends FieldsObject {
		val suggId = new IntDatabaseField(self, "SUGG_ID")
		val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.suggId
}