package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassSignup extends StorableClass(JpClassSignup) {
	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, JpClassSignup.fields.instanceId)
		val personId = new IntFieldValue(self, JpClassSignup.fields.personId)
		val signupDatetime = new NullableLocalDateTimeFieldValue(self, JpClassSignup.fields.signupDatetime)
		val sequence = new NullableDoubleFieldValue(self, JpClassSignup.fields.sequence)
		val signupType = new NullableBooleanFIeldValue(self, JpClassSignup.fields.signupType)
		val groupId = new NullableIntFieldValue(self, JpClassSignup.fields.groupId)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassSignup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassSignup.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassSignup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassSignup.fields.updatedBy)
		val isReserve = new NullableBooleanFIeldValue(self, JpClassSignup.fields.isReserve)
		val orderId = new NullableIntFieldValue(self, JpClassSignup.fields.orderId)
		val price = new NullableDoubleFieldValue(self, JpClassSignup.fields.price)
		val closeId = new NullableIntFieldValue(self, JpClassSignup.fields.closeId)
		val discountInstanceId = new NullableIntFieldValue(self, JpClassSignup.fields.discountInstanceId)
		val paymentMedium = new NullableStringFieldValue(self, JpClassSignup.fields.paymentMedium)
		val ccTransNum = new NullableStringFieldValue(self, JpClassSignup.fields.ccTransNum)
		val voidCloseId = new NullableIntFieldValue(self, JpClassSignup.fields.voidCloseId)
		val signupNote = new NullableStringFieldValue(self, JpClassSignup.fields.signupNote)
		val ignoreIllegalSignup = new NullableBooleanFIeldValue(self, JpClassSignup.fields.ignoreIllegalSignup)
		val staggerId = new NullableIntFieldValue(self, JpClassSignup.fields.staggerId)
		val parentSignupNote = new NullableStringFieldValue(self, JpClassSignup.fields.parentSignupNote)
		val sectionId = new NullableIntFieldValue(self, JpClassSignup.fields.sectionId)
		val sectionLookupId = new NullableIntFieldValue(self, JpClassSignup.fields.sectionLookupId)
	}
}

object JpClassSignup extends StorableObject[JpClassSignup] {
	val entityName: String = "JP_CLASS_SIGNUPS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val signupDatetime = new NullableLocalDateTimeDatabaseField(self, "SIGNUP_DATETIME")
		val sequence = new NullableDoubleDatabaseField(self, "SEQUENCE")
		val signupType = new NullableBooleanDatabaseField(self, "SIGNUP_TYPE")
		val groupId = new NullableIntDatabaseField(self, "GROUP_ID")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val isReserve = new NullableBooleanDatabaseField(self, "IS_RESERVE")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val ccTransNum = new NullableStringDatabaseField(self, "CC_TRANS_NUM", 10)
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val signupNote = new NullableStringDatabaseField(self, "SIGNUP_NOTE", 4000)
		val ignoreIllegalSignup = new NullableBooleanDatabaseField(self, "IGNORE_ILLEGAL_SIGNUP")
		val staggerId = new NullableIntDatabaseField(self, "STAGGER_ID")
		val parentSignupNote = new NullableStringDatabaseField(self, "PARENT_SIGNUP_NOTE", 4000)
		val sectionId = new NullableIntDatabaseField(self, "SECTION_ID")
		val sectionLookupId = new NullableIntDatabaseField(self, "SECTION_LOOKUP_ID")
	}

	def primaryKey: IntDatabaseField = fields.signupId
}