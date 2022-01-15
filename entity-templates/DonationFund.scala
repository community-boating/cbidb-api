package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DonationFund extends StorableClass(DonationFund) {
	object values extends ValuesObject {
		val fundId = new IntFieldValue(self, DonationFund.fields.fundId)
		val fundName = new NullableStringFieldValue(self, DonationFund.fields.fundName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, DonationFund.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DonationFund.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, DonationFund.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DonationFund.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, DonationFund.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, DonationFund.fields.displayOrder)
		val letterText = new NullableStringFieldValue(self, DonationFund.fields.letterText)
		val showInCheckout = new NullableBooleanFIeldValue(self, DonationFund.fields.showInCheckout)
		val portalDescription = new NullableStringFieldValue(self, DonationFund.fields.portalDescription)
		val isEndowment = new NullableBooleanFIeldValue(self, DonationFund.fields.isEndowment)
	}
}

object DonationFund extends StorableObject[DonationFund] {
	val entityName: String = "DONATION_FUNDS"

	object fields extends FieldsObject {
		val fundId = new IntDatabaseField(self, "FUND_ID")
		val fundName = new NullableStringDatabaseField(self, "FUND_NAME", 100)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val letterText = new NullableStringDatabaseField(self, "LETTER_TEXT", 500)
		val showInCheckout = new NullableBooleanDatabaseField(self, "SHOW_IN_CHECKOUT")
		val portalDescription = new NullableStringDatabaseField(self, "PORTAL_DESCRIPTION", 2000)
		val isEndowment = new NullableBooleanDatabaseField(self, "IS_ENDOWMENT")
	}

	def primaryKey: IntDatabaseField = fields.fundId
}