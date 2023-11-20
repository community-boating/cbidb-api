package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DonationSuggestion extends StorableClass(DonationSuggestion) {
	override object values extends ValuesObject {
		val suggId = new IntFieldValue(self, DonationSuggestion.fields.suggId)
		val amount = new DoubleFieldValue(self, DonationSuggestion.fields.amount)
		val createdOn = new DateTimeFieldValue(self, DonationSuggestion.fields.createdOn)
		val createdBy = new StringFieldValue(self, DonationSuggestion.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, DonationSuggestion.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, DonationSuggestion.fields.updatedBy)
	}
}

object DonationSuggestion extends StorableObject[DonationSuggestion] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DONATION_SUGGESTIONS"

	object fields extends FieldsObject {
		val suggId = new IntDatabaseField(self, "SUGG_ID")
		val amount = new DoubleDatabaseField(self, "AMOUNT")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.suggId
}