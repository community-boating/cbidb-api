package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DonationFund extends StorableClass(DonationFund) {
	object values extends ValuesObject {
		val fundId = new IntFieldValue(self, DonationFund.fields.fundId)
		val fundName = new StringFieldValue(self, DonationFund.fields.fundName)
		val active = new BooleanFieldValue(self, DonationFund.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, DonationFund.fields.displayOrder)
		val letterText = new NullableStringFieldValue(self, DonationFund.fields.letterText)
		val showInCheckout = new BooleanFieldValue(self, DonationFund.fields.showInCheckout)
		val portalDescription = new NullableStringFieldValue(self, DonationFund.fields.portalDescription)
		val isEndowment = new BooleanFieldValue(self, DonationFund.fields.isEndowment)
	}
}

object DonationFund extends StorableObject[DonationFund] {
	override val entityName: String = "DONATION_FUNDS"

	object fields extends FieldsObject {
		val fundId = new IntDatabaseField(self, "FUND_ID")
		@NullableInDatabase
		val fundName = new StringDatabaseField(self, "FUND_NAME", 100)
		val active = new BooleanDatabaseField(self, "ACTIVE", true)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val letterText = new NullableStringDatabaseField(self, "LETTER_TEXT", 500)
		val showInCheckout = new BooleanDatabaseField(self, "SHOW_IN_CHECKOUT", true)
		val portalDescription = new NullableStringDatabaseField(self, "PORTAL_DESCRIPTION", 2000)
		val isEndowment = new BooleanDatabaseField(self, "IS_ENDOWMENT", true)
	}

	override def primaryKey: IntDatabaseField = fields.fundId
}
