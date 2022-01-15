package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class StripeRefund extends StorableClass(StripeRefund) {
	object values extends ValuesObject {
		val refundId = new StringFieldValue(self, StripeRefund.fields.refundId)
		val chargeId = new NullableStringFieldValue(self, StripeRefund.fields.chargeId)
		val closeId = new NullableIntFieldValue(self, StripeRefund.fields.closeId)
		val amountInCents = new NullableDoubleFieldValue(self, StripeRefund.fields.amountInCents)
	}
}

object StripeRefund extends StorableObject[StripeRefund] {
	val entityName: String = "STRIPE_REFUNDS"

	object fields extends FieldsObject {
		val refundId = new StringDatabaseField(self, "REFUND_ID", 50)
		val chargeId = new NullableStringDatabaseField(self, "CHARGE_ID", 50)
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val amountInCents = new NullableDoubleDatabaseField(self, "AMOUNT_IN_CENTS")
	}

	def primaryKey: IntDatabaseField = fields.refundId
}