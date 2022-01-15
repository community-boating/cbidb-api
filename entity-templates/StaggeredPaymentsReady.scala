package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class StaggeredPaymentsReady extends StorableClass(StaggeredPaymentsReady) {
	object values extends ValuesObject {
		val orderId = new IntFieldValue(self, StaggeredPaymentsReady.fields.orderId)
		val staggerId = new IntFieldValue(self, StaggeredPaymentsReady.fields.staggerId)
		val seq = new DoubleFieldValue(self, StaggeredPaymentsReady.fields.seq)
		val expectedPaymentDate = new LocalDateTimeFieldValue(self, StaggeredPaymentsReady.fields.expectedPaymentDate)
		val rawAmountInCents = new NullableDoubleFieldValue(self, StaggeredPaymentsReady.fields.rawAmountInCents)
		val addlAmountInCents = new NullableDoubleFieldValue(self, StaggeredPaymentsReady.fields.addlAmountInCents)
		val paymentIntentRowId = new NullableIntFieldValue(self, StaggeredPaymentsReady.fields.paymentIntentRowId)
		val failedCron = new NullableBooleanFIeldValue(self, StaggeredPaymentsReady.fields.failedCron)
	}
}

object StaggeredPaymentsReady extends StorableObject[StaggeredPaymentsReady] {
	val entityName: String = "STAGGERED_PAYMENTS_READY"

	object fields extends FieldsObject {
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val staggerId = new IntDatabaseField(self, "STAGGER_ID")
		val seq = new DoubleDatabaseField(self, "SEQ")
		val expectedPaymentDate = new LocalDateTimeDatabaseField(self, "EXPECTED_PAYMENT_DATE")
		val rawAmountInCents = new NullableDoubleDatabaseField(self, "RAW_AMOUNT_IN_CENTS")
		val addlAmountInCents = new NullableDoubleDatabaseField(self, "ADDL_AMOUNT_IN_CENTS")
		val paymentIntentRowId = new NullableIntDatabaseField(self, "PAYMENT_INTENT_ROW_ID")
		val failedCron = new NullableBooleanDatabaseField(self, "FAILED_CRON")
	}
}