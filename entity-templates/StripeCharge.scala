package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class StripeCharge extends StorableClass(StripeCharge) {
	object values extends ValuesObject {
		val chargeId = new StringFieldValue(self, StripeCharge.fields.chargeId)
		val amountInCents = new NullableDoubleFieldValue(self, StripeCharge.fields.amountInCents)
		val createdEpoch = new NullableDoubleFieldValue(self, StripeCharge.fields.createdEpoch)
		val closeId = new NullableIntFieldValue(self, StripeCharge.fields.closeId)
		val orderId = new NullableIntFieldValue(self, StripeCharge.fields.orderId)
		val token = new NullableStringFieldValue(self, StripeCharge.fields.token)
		val paid = new NullableBooleanFIeldValue(self, StripeCharge.fields.paid)
		val status = new NullableStringFieldValue(self, StripeCharge.fields.status)
		val createdDatetime = new NullableLocalDateTimeFieldValue(self, StripeCharge.fields.createdDatetime)
		val refunds = new NullableStringFieldValue(self, StripeCharge.fields.refunds)
		val description = new NullableStringFieldValue(self, StripeCharge.fields.description)
	}
}

object StripeCharge extends StorableObject[StripeCharge] {
	val entityName: String = "STRIPE_CHARGES"

	object fields extends FieldsObject {
		val chargeId = new StringDatabaseField(self, "CHARGE_ID", 50)
		val amountInCents = new NullableDoubleDatabaseField(self, "AMOUNT_IN_CENTS")
		val createdEpoch = new NullableDoubleDatabaseField(self, "CREATED_EPOCH")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val token = new NullableStringDatabaseField(self, "TOKEN", 50)
		val paid = new NullableBooleanDatabaseField(self, "PAID")
		val status = new NullableStringDatabaseField(self, "STATUS", 20)
		val createdDatetime = new NullableLocalDateTimeDatabaseField(self, "CREATED_DATETIME")
		val refunds = new NullableStringDatabaseField(self, "REFUNDS", 200)
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 200)
	}

	def primaryKey: IntDatabaseField = fields.chargeId
}