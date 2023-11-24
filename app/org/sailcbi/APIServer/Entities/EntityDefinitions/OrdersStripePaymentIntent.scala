package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class OrdersStripePaymentIntent extends StorableClass(OrdersStripePaymentIntent) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, OrdersStripePaymentIntent.fields.rowId)
		val orderId = new IntFieldValue(self, OrdersStripePaymentIntent.fields.orderId)
		val paymentIntentId = new StringFieldValue(self, OrdersStripePaymentIntent.fields.paymentIntentId)
		val amountInCents = new DoubleFieldValue(self, OrdersStripePaymentIntent.fields.amountInCents)
		val paid = new BooleanFieldValue(self, OrdersStripePaymentIntent.fields.paid)
		val paidCloseId = new IntFieldValue(self, OrdersStripePaymentIntent.fields.paidCloseId)
	}
}

object OrdersStripePaymentIntent extends StorableObject[OrdersStripePaymentIntent] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "ORDERS_STRIPE_PAYMENT_INTENTS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val paymentIntentId = new StringDatabaseField(self, "PAYMENT_INTENT_ID", 50)
		val amountInCents = new DoubleDatabaseField(self, "AMOUNT_IN_CENTS")
		val paid = new BooleanDatabaseField(self, "PAID", false)
		val paidCloseId = new IntDatabaseField(self, "PAID_CLOSE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}