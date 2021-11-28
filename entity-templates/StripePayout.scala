package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class StripePayout extends StorableClass(StripePayout) {
	object values extends ValuesObject {
		val payoutId = new StringFieldValue(self, StripePayout.fields.payoutId)
		val amountInCents = new NullableDoubleFieldValue(self, StripePayout.fields.amountInCents)
		val arrivalDatetime = new NullableLocalDateTimeFieldValue(self, StripePayout.fields.arrivalDatetime)
		val balanceTransactionId = new NullableStringFieldValue(self, StripePayout.fields.balanceTransactionId)
		val status = new NullableStringFieldValue(self, StripePayout.fields.status)
	}
}

object StripePayout extends StorableObject[StripePayout] {
	val entityName: String = "STRIPE_PAYOUTS"

	object fields extends FieldsObject {
		val payoutId = new StringDatabaseField(self, "PAYOUT_ID", 50)
		val amountInCents = new NullableDoubleDatabaseField(self, "AMOUNT_IN_CENTS")
		val arrivalDatetime = new NullableLocalDateTimeDatabaseField(self, "ARRIVAL_DATETIME")
		val balanceTransactionId = new NullableStringDatabaseField(self, "BALANCE_TRANSACTION_ID", 50)
		val status = new NullableStringDatabaseField(self, "STATUS", 50)
	}

	def primaryKey: IntDatabaseField = fields.payoutId
}