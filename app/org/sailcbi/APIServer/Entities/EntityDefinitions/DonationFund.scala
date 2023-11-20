package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DonationFund extends StorableClass(DonationFund) {
	override object values extends ValuesObject {
		val fundId = new IntFieldValue(self, DonationFund.fields.fundId)
		val fundName = new StringFieldValue(self, DonationFund.fields.fundName)
		val createdOn = new DateTimeFieldValue(self, DonationFund.fields.createdOn)
		val createdBy = new StringFieldValue(self, DonationFund.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, DonationFund.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, DonationFund.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, DonationFund.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, DonationFund.fields.displayOrder)
		val letterText = new NullableStringFieldValue(self, DonationFund.fields.letterText)
		val showInCheckout = new NullableBooleanFieldValue(self, DonationFund.fields.showInCheckout)
		val isEndowment = new NullableBooleanFieldValue(self, DonationFund.fields.isEndowment)
		val portalDescription = new NullableStringFieldValue(self, DonationFund.fields.portalDescription)
	}
}

object DonationFund extends StorableObject[DonationFund] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DONATION_FUNDS"

	object fields extends FieldsObject {
		val fundId = new IntDatabaseField(self, "FUND_ID")
		val fundName = new StringDatabaseField(self, "FUND_NAME", 100)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val letterText = new NullableStringDatabaseField(self, "LETTER_TEXT", 500)
		val showInCheckout = new NullableBooleanDatabaseField(self, "SHOW_IN_CHECKOUT")
		val isEndowment = new NullableBooleanDatabaseField(self, "IS_ENDOWMENT")
		val portalDescription = new NullableStringDatabaseField(self, "PORTAL_DESCRIPTION", 2000)
	}

	def primaryKey: IntDatabaseField = fields.fundId
}