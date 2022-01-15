package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class OrderNumber extends StorableClass(OrderNumber) {
	object values extends ValuesObject {
		val orderId = new IntFieldValue(self, OrderNumber.fields.orderId)
		val personId = new IntFieldValue(self, OrderNumber.fields.personId)
		val orderNum = new StringFieldValue(self, OrderNumber.fields.orderNum)
		val approvedTransId = new NullableIntFieldValue(self, OrderNumber.fields.approvedTransId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, OrderNumber.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, OrderNumber.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, OrderNumber.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, OrderNumber.fields.updatedBy)
		val processedDate = new NullableLocalDateTimeFieldValue(self, OrderNumber.fields.processedDate)
		val approvedStripeChargeAtt = new NullableDoubleFieldValue(self, OrderNumber.fields.approvedStripeChargeAtt)
		val addlStaggeredPayments = new NullableDoubleFieldValue(self, OrderNumber.fields.addlStaggeredPayments)
		val appAlias = new NullableStringFieldValue(self, OrderNumber.fields.appAlias)
		val usePaymentIntent = new NullableBooleanFIeldValue(self, OrderNumber.fields.usePaymentIntent)
	}
}

object OrderNumber extends StorableObject[OrderNumber] {
	val entityName: String = "ORDER_NUMBERS"

	object fields extends FieldsObject {
		val orderId = new IntDatabaseField(self, "ORDER_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val orderNum = new StringDatabaseField(self, "ORDER_NUM", 25)
		val approvedTransId = new NullableIntDatabaseField(self, "APPROVED_TRANS_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val processedDate = new NullableLocalDateTimeDatabaseField(self, "PROCESSED_DATE")
		val approvedStripeChargeAtt = new NullableDoubleDatabaseField(self, "APPROVED_STRIPE_CHARGE_ATT")
		val addlStaggeredPayments = new NullableDoubleDatabaseField(self, "ADDL_STAGGERED_PAYMENTS")
		val appAlias = new NullableStringDatabaseField(self, "APP_ALIAS", 20)
		val usePaymentIntent = new NullableBooleanDatabaseField(self, "USE_PAYMENT_INTENT")
	}

	def primaryKey: IntDatabaseField = fields.orderId
}