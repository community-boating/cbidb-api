package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class OrderStaggeredPayment extends StorableClass(OrderStaggeredPayment) {
	override object values extends ValuesObject {
		val staggerId = new IntFieldValue(self, OrderStaggeredPayment.fields.staggerId)
		val orderId = new IntFieldValue(self, OrderStaggeredPayment.fields.orderId)
		val seq = new IntFieldValue(self, OrderStaggeredPayment.fields.seq)
		val expectedPaymentDate = new DateTimeFieldValue(self, OrderStaggeredPayment.fields.expectedPaymentDate)
		val paid = new BooleanFieldValue(self, OrderStaggeredPayment.fields.paid)
		val approvedStripeChargeAtt = new NullableIntFieldValue(self, OrderStaggeredPayment.fields.approvedStripeChargeAtt)
		val paidCloseId = new NullableIntFieldValue(self, OrderStaggeredPayment.fields.paidCloseId)
		val redeemedCloseId = new NullableIntFieldValue(self, OrderStaggeredPayment.fields.redeemedCloseId)
		val rawAmountInCents = new IntFieldValue(self, OrderStaggeredPayment.fields.rawAmountInCents)
		val addlAmountInCents = new IntFieldValue(self, OrderStaggeredPayment.fields.addlAmountInCents)
		val paymentIntentRowId = new NullableIntFieldValue(self, OrderStaggeredPayment.fields.paymentIntentRowId)
		val failedCron = new NullableBooleanFieldValue(self, OrderStaggeredPayment.fields.failedCron)
	}
}

object OrderStaggeredPayment extends StorableObject[OrderStaggeredPayment] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "ORDER_STAGGERED_PAYMENTS"

	object fields extends FieldsObject {
		val staggerId = new IntDatabaseField(self, "STAGGER_ID")
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val seq = new IntDatabaseField(self, "SEQ")
		val expectedPaymentDate = new DateTimeDatabaseField(self, "EXPECTED_PAYMENT_DATE")
		val paid = new BooleanDatabaseField(self, "PAID", false)
		val approvedStripeChargeAtt = new NullableIntDatabaseField(self, "APPROVED_STRIPE_CHARGE_ATT")
		val paidCloseId = new NullableIntDatabaseField(self, "PAID_CLOSE_ID")
		val redeemedCloseId = new NullableIntDatabaseField(self, "REDEEMED_CLOSE_ID")
		@NullableInDatabase
		val rawAmountInCents = new IntDatabaseField(self, "RAW_AMOUNT_IN_CENTS")
		@NullableInDatabase
		val addlAmountInCents = new IntDatabaseField(self, "ADDL_AMOUNT_IN_CENTS")
		val paymentIntentRowId = new NullableIntDatabaseField(self, "PAYMENT_INTENT_ROW_ID")
		val failedCron = new NullableBooleanDatabaseField(self, "FAILED_CRON")
	}

	def primaryKey: IntDatabaseField = fields.staggerId
}