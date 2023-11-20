package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Donation extends StorableClass(Donation) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
	}

	override object values extends ValuesObject {
		val donationId = new IntFieldValue(self, Donation.fields.donationId)
		val personId = new IntFieldValue(self, Donation.fields.personId)
		val addressLine = new NullableStringFieldValue(self, Donation.fields.addressLine)
		val amount = new NullableDoubleFieldValue(self, Donation.fields.amount)
		val description = new NullableStringFieldValue(self, Donation.fields.description)
		val donationDate = new DateTimeFieldValue(self, Donation.fields.donationDate)
		val inKind = new NullableBooleanFieldValue(self, Donation.fields.inKind)
		val restriction = new NullableStringFieldValue(self, Donation.fields.restriction)
		val salutation = new NullableStringFieldValue(self, Donation.fields.salutation)
		val createdOn = new NullableDateTimeFieldValue(self, Donation.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Donation.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, Donation.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Donation.fields.updatedBy)
		val orderId = new NullableIntFieldValue(self, Donation.fields.orderId)
		val fundId = new IntFieldValue(self, Donation.fields.fundId)
		val initiativeId = new IntFieldValue(self, Donation.fields.initiativeId)
		val closeId = new NullableIntFieldValue(self, Donation.fields.closeId)
		val ccTransNum = new NullableDoubleFieldValue(self, Donation.fields.ccTransNum)
		val paymentLocation = new NullableStringFieldValue(self, Donation.fields.paymentLocation)
		val paymentMedium = new NullableStringFieldValue(self, Donation.fields.paymentMedium)
		val voidCloseId = new NullableIntFieldValue(self, Donation.fields.voidCloseId)
		val noAck = new NullableBooleanFieldValue(self, Donation.fields.noAck)
		val inMemoryOf = new NullableStringFieldValue(self, Donation.fields.inMemoryOf)
		val isMatch = new NullableBooleanFieldValue(self, Donation.fields.isMatch)
		val skipAck = new NullableBooleanFieldValue(self, Donation.fields.skipAck)
	}
}

object Donation extends StorableObject[Donation] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DONATIONS"

	object fields extends FieldsObject {
		val donationId = new IntDatabaseField(self, "DONATION_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val addressLine = new NullableStringDatabaseField(self, "ADDRESS_LINE", 200)
		val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 500)
		val donationDate = new DateTimeDatabaseField(self, "DONATION_DATE")
		val inKind = new NullableBooleanDatabaseField(self, "IN_KIND")
		val restriction = new NullableStringDatabaseField(self, "RESTRICTION", 200)
		val salutation = new NullableStringDatabaseField(self, "SALUTATION", 200)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val fundId = new IntDatabaseField(self, "FUND_ID")
		val initiativeId = new IntDatabaseField(self, "INITIATIVE_ID")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val ccTransNum = new NullableDoubleDatabaseField(self, "CC_TRANS_NUM")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 100)
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 100)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val noAck = new NullableBooleanDatabaseField(self, "NO_ACK")
		val inMemoryOf = new NullableStringDatabaseField(self, "IN_MEMORY_OF", 500)
		val isMatch = new NullableBooleanDatabaseField(self, "IS_MATCH")
		val skipAck = new NullableBooleanDatabaseField(self, "SKIP_ACK")
	}

	def primaryKey: IntDatabaseField = fields.donationId
}