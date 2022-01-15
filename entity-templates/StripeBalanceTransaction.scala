package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class StripeBalanceTransaction extends StorableClass(StripeBalanceTransaction) {
	object values extends ValuesObject {
		val transactionId = new StringFieldValue(self, StripeBalanceTransaction.fields.transactionId)
		val amountInCents = new NullableDoubleFieldValue(self, StripeBalanceTransaction.fields.amountInCents)
		val description = new NullableStringFieldValue(self, StripeBalanceTransaction.fields.description)
		val feeInCents = new NullableDoubleFieldValue(self, StripeBalanceTransaction.fields.feeInCents)
		val netInCents = new NullableDoubleFieldValue(self, StripeBalanceTransaction.fields.netInCents)
		val source = new NullableStringFieldValue(self, StripeBalanceTransaction.fields.source)
		val status = new NullableStringFieldValue(self, StripeBalanceTransaction.fields.status)
		val type = new NullableStringFieldValue(self, StripeBalanceTransaction.fields.type)
		val payout = new NullableStringFieldValue(self, StripeBalanceTransaction.fields.payout)
		val created = new NullableDoubleFieldValue(self, StripeBalanceTransaction.fields.created)
	}
}

object StripeBalanceTransaction extends StorableObject[StripeBalanceTransaction] {
	val entityName: String = "STRIPE_BALANCE_TRANSACTIONS"

	object fields extends FieldsObject {
		val transactionId = new StringDatabaseField(self, "TRANSACTION_ID", 50)
		val amountInCents = new NullableDoubleDatabaseField(self, "AMOUNT_IN_CENTS")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 500)
		val feeInCents = new NullableDoubleDatabaseField(self, "FEE_IN_CENTS")
		val netInCents = new NullableDoubleDatabaseField(self, "NET_IN_CENTS")
		val source = new NullableStringDatabaseField(self, "SOURCE", 50)
		val status = new NullableStringDatabaseField(self, "STATUS", 20)
		val type = new NullableStringDatabaseField(self, "TYPE", 20)
		val payout = new NullableStringDatabaseField(self, "PAYOUT", 50)
		val created = new NullableDoubleDatabaseField(self, "CREATED")
	}

	def primaryKey: IntDatabaseField = fields.transactionId
}