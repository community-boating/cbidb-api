package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassSignup extends StorableClass(ApClassSignup) {
	override object references extends ReferencesObject {
		val apClassInstance = new Initializable[ApClassInstance]
		val person = new Initializable[Person]
		val apClassWaitlistResult = new Initializable[ApClassWaitlistResult]
	}

	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, ApClassSignup.fields.instanceId)
		val personId = new IntFieldValue(self, ApClassSignup.fields.personId)
		val orderId = new NullableIntFieldValue(self, ApClassSignup.fields.orderId)
		val signupDatetime = new DateTimeFieldValue(self, ApClassSignup.fields.signupDatetime)
		val sequence = new IntFieldValue(self, ApClassSignup.fields.sequence)
		val signupType = new StringFieldValue(self, ApClassSignup.fields.signupType)
		val price = new NullableDoubleFieldValue(self, ApClassSignup.fields.price)
		val closeId = new NullableIntFieldValue(self, ApClassSignup.fields.closeId)
		val discountInstanceId = new NullableIntFieldValue(self, ApClassSignup.fields.discountInstanceId)
		val paymentMedium = new NullableStringFieldValue(self, ApClassSignup.fields.paymentMedium)
		val ccTransNum = new NullableStringFieldValue(self, ApClassSignup.fields.ccTransNum)
		val voidCloseId = new NullableIntFieldValue(self, ApClassSignup.fields.voidCloseId)
		val signupNote = new NullableStringFieldValue(self, ApClassSignup.fields.signupNote)
		val voidedOnline = new BooleanFieldValue(self, ApClassSignup.fields.voidedOnline)
		val paymentLocation = new NullableStringFieldValue(self, ApClassSignup.fields.paymentLocation)
	}
}

object ApClassSignup extends StorableObject[ApClassSignup] {
	val entityName: String = "AP_CLASS_SIGNUPS"

	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val signupDatetime = new DateTimeDatabaseField(self, "SIGNUP_DATETIME")
		val sequence = new IntDatabaseField(self, "SEQUENCE")
		val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableStringDatabaseField(self, "CC_TRANS_NUM", 50)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val signupNote = new NullableStringDatabaseField(self, "SIGNUP_NOTE", 4000)
		val voidedOnline = new BooleanDatabaseField(self, "VOIDED_ONLINE", true)
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}