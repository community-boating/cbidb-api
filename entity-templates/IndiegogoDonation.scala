package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class IndiegogoDonation extends StorableClass(IndiegogoDonation) {
	object values extends ValuesObject {
		val id = new IntFieldValue(self, IndiegogoDonation.fields.id)
		val perkId = new NullableStringFieldValue(self, IndiegogoDonation.fields.perkId)
		val pledgeId = new NullableIntFieldValue(self, IndiegogoDonation.fields.pledgeId)
		val fulfillmentStatus = new NullableStringFieldValue(self, IndiegogoDonation.fields.fulfillmentStatus)
		val fundingDate = new NullableStringFieldValue(self, IndiegogoDonation.fields.fundingDate)
		val paymentMethod = new NullableStringFieldValue(self, IndiegogoDonation.fields.paymentMethod)
		val appearance = new NullableStringFieldValue(self, IndiegogoDonation.fields.appearance)
		val name = new NullableStringFieldValue(self, IndiegogoDonation.fields.name)
		val email = new NullableStringFieldValue(self, IndiegogoDonation.fields.email)
		val amount = new NullableStringFieldValue(self, IndiegogoDonation.fields.amount)
		val perk = new NullableStringFieldValue(self, IndiegogoDonation.fields.perk)
		val shippingName = new NullableStringFieldValue(self, IndiegogoDonation.fields.shippingName)
		val shippingAddress = new NullableStringFieldValue(self, IndiegogoDonation.fields.shippingAddress)
		val shippingAddress2 = new NullableStringFieldValue(self, IndiegogoDonation.fields.shippingAddress2)
		val shippingCity = new NullableStringFieldValue(self, IndiegogoDonation.fields.shippingCity)
		val shippingStateprovince = new NullableStringFieldValue(self, IndiegogoDonation.fields.shippingStateprovince)
		val shippingZippostalCode = new NullableStringFieldValue(self, IndiegogoDonation.fields.shippingZippostalCode)
		val shippingCountry = new NullableStringFieldValue(self, IndiegogoDonation.fields.shippingCountry)
		val amountNumeric = new NullableDoubleFieldValue(self, IndiegogoDonation.fields.amountNumeric)
		val instance = new DoubleFieldValue(self, IndiegogoDonation.fields.instance)
	}
}

object IndiegogoDonation extends StorableObject[IndiegogoDonation] {
	val entityName: String = "INDIEGOGO_DONATIONS"

	object fields extends FieldsObject {
		val id = new IntDatabaseField(self, "ID")
		val perkId = new NullableStringDatabaseField(self, "PERK_ID", 30)
		val pledgeId = new NullableIntDatabaseField(self, "PLEDGE_ID")
		val fulfillmentStatus = new NullableStringDatabaseField(self, "FULFILLMENT_STATUS", 60)
		val fundingDate = new NullableStringDatabaseField(self, "FUNDING_DATE", 30)
		val paymentMethod = new NullableStringDatabaseField(self, "PAYMENT_METHOD", 30)
		val appearance = new NullableStringDatabaseField(self, "APPEARANCE", 30)
		val name = new NullableStringDatabaseField(self, "NAME", 1)
		val email = new NullableStringDatabaseField(self, "EMAIL", 255)
		val amount = new NullableStringDatabaseField(self, "AMOUNT", 30)
		val perk = new NullableStringDatabaseField(self, "PERK", 255)
		val shippingName = new NullableStringDatabaseField(self, "SHIPPING_NAME", 30)
		val shippingAddress = new NullableStringDatabaseField(self, "SHIPPING_ADDRESS", 255)
		val shippingAddress2 = new NullableStringDatabaseField(self, "SHIPPING_ADDRESS_2", 30)
		val shippingCity = new NullableStringDatabaseField(self, "SHIPPING_CITY", 30)
		val shippingStateprovince = new NullableStringDatabaseField(self, "SHIPPING_STATEPROVINCE", 30)
		val shippingZippostalCode = new NullableStringDatabaseField(self, "SHIPPING_ZIPPOSTAL_CODE", 30)
		val shippingCountry = new NullableStringDatabaseField(self, "SHIPPING_COUNTRY", 30)
		val amountNumeric = new NullableDoubleDatabaseField(self, "AMOUNT_NUMERIC")
		val instance = new DoubleDatabaseField(self, "INSTANCE")
	}

	def primaryKey: IntDatabaseField = fields.id
}