package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassSignup extends StorableClass(ApClassSignup) {
	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassSignup.fields.signupId)
		val instanceId = new NullableIntFieldValue(self, ApClassSignup.fields.instanceId)
		val personId = new NullableIntFieldValue(self, ApClassSignup.fields.personId)
		val orderId = new NullableIntFieldValue(self, ApClassSignup.fields.orderId)
		val signupDatetime = new NullableLocalDateTimeFieldValue(self, ApClassSignup.fields.signupDatetime)
		val sequence = new NullableDoubleFieldValue(self, ApClassSignup.fields.sequence)
		val signupType = new NullableBooleanFIeldValue(self, ApClassSignup.fields.signupType)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassSignup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassSignup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassSignup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassSignup.fields.updatedBy)
		val price = new NullableDoubleFieldValue(self, ApClassSignup.fields.price)
		val closeId = new NullableIntFieldValue(self, ApClassSignup.fields.closeId)
		val discountInstanceId = new NullableIntFieldValue(self, ApClassSignup.fields.discountInstanceId)
		val paymentMedium = new NullableStringFieldValue(self, ApClassSignup.fields.paymentMedium)
		val ccTransNum = new NullableStringFieldValue(self, ApClassSignup.fields.ccTransNum)
		val voidCloseId = new NullableIntFieldValue(self, ApClassSignup.fields.voidCloseId)
		val signupNote = new NullableStringFieldValue(self, ApClassSignup.fields.signupNote)
		val voidedOnline = new NullableBooleanFIeldValue(self, ApClassSignup.fields.voidedOnline)
		val paymentLocation = new NullableStringFieldValue(self, ApClassSignup.fields.paymentLocation)
	}
}

object ApClassSignup extends StorableObject[ApClassSignup] {
	val entityName: String = "AP_CLASS_SIGNUPS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new NullableIntDatabaseField(self, "INSTANCE_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val signupDatetime = new NullableLocalDateTimeDatabaseField(self, "SIGNUP_DATETIME")
		val sequence = new NullableDoubleDatabaseField(self, "SEQUENCE")
		val signupType = new NullableBooleanDatabaseField(self, "SIGNUP_TYPE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableStringDatabaseField(self, "CC_TRANS_NUM", 50)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val signupNote = new NullableStringDatabaseField(self, "SIGNUP_NOTE", 4000)
		val voidedOnline = new NullableBooleanDatabaseField(self, "VOIDED_ONLINE")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}