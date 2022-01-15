package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class StripeChargeAttLog extends StorableClass(StripeChargeAttLog) {
	object values extends ValuesObject {
		val attemptId = new IntFieldValue(self, StripeChargeAttLog.fields.attemptId)
		val attemptDatetime = new NullableLocalDateTimeFieldValue(self, StripeChargeAttLog.fields.attemptDatetime)
		val tokenId = new NullableIntFieldValue(self, StripeChargeAttLog.fields.tokenId)
		val cartTotal = new NullableDoubleFieldValue(self, StripeChargeAttLog.fields.cartTotal)
		val result = new NullableBooleanFIeldValue(self, StripeChargeAttLog.fields.result)
		val createdOn = new NullableLocalDateTimeFieldValue(self, StripeChargeAttLog.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, StripeChargeAttLog.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, StripeChargeAttLog.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, StripeChargeAttLog.fields.updatedBy)
		val errorType = new NullableStringFieldValue(self, StripeChargeAttLog.fields.errorType)
		val errorMsg = new NullableStringFieldValue(self, StripeChargeAttLog.fields.errorMsg)
		val chargeNumber = new NullableStringFieldValue(self, StripeChargeAttLog.fields.chargeNumber)
		val chargeTotal = new NullableDoubleFieldValue(self, StripeChargeAttLog.fields.chargeTotal)
		val orderId = new NullableIntFieldValue(self, StripeChargeAttLog.fields.orderId)
		val paymentIntentRowId = new NullableIntFieldValue(self, StripeChargeAttLog.fields.paymentIntentRowId)
	}
}

object StripeChargeAttLog extends StorableObject[StripeChargeAttLog] {
	val entityName: String = "STRIPE_CHARGE_ATT_LOG"

	object fields extends FieldsObject {
		val attemptId = new IntDatabaseField(self, "ATTEMPT_ID")
		val attemptDatetime = new NullableLocalDateTimeDatabaseField(self, "ATTEMPT_DATETIME")
		val tokenId = new NullableIntDatabaseField(self, "TOKEN_ID")
		val cartTotal = new NullableDoubleDatabaseField(self, "CART_TOTAL")
		val result = new NullableBooleanDatabaseField(self, "RESULT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val errorType = new NullableStringDatabaseField(self, "ERROR_TYPE", 100)
		val errorMsg = new NullableStringDatabaseField(self, "ERROR_MSG", 4000)
		val chargeNumber = new NullableStringDatabaseField(self, "CHARGE_NUMBER", 100)
		val chargeTotal = new NullableDoubleDatabaseField(self, "CHARGE_TOTAL")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val paymentIntentRowId = new NullableIntDatabaseField(self, "PAYMENT_INTENT_ROW_ID")
	}

	def primaryKey: IntDatabaseField = fields.attemptId
}