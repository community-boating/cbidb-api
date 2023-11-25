package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpClassSignup extends StorableClass(JpClassSignup) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
		val jpClassWlResult = new Initializable[Option[JpClassWlResult]]
		val person = new Initializable[Person]
		val group = new Initializable[Option[JpGroup]]
		val section = new Initializable[Option[JpClassSection]]
	}

	override object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, JpClassSignup.fields.instanceId)
		val personId = new IntFieldValue(self, JpClassSignup.fields.personId)
		val signupDatetime = new DateTimeFieldValue(self, JpClassSignup.fields.signupDatetime)
		val sequence = new DoubleFieldValue(self, JpClassSignup.fields.sequence)
		val signupType = new StringFieldValue(self, JpClassSignup.fields.signupType)
		val groupId = new NullableIntFieldValue(self, JpClassSignup.fields.groupId)
		val createdOn = new DateTimeFieldValue(self, JpClassSignup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassSignup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassSignup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassSignup.fields.updatedBy)
		val isReserve = new NullableBooleanFieldValue(self, JpClassSignup.fields.isReserve)
		val ccTransNum = new NullableStringFieldValue(self, JpClassSignup.fields.ccTransNum)
		val closeId = new NullableIntFieldValue(self, JpClassSignup.fields.closeId)
		val discountInstanceId = new NullableIntFieldValue(self, JpClassSignup.fields.discountInstanceId)
		val orderId = new NullableIntFieldValue(self, JpClassSignup.fields.orderId)
		val paymentMedium = new NullableStringFieldValue(self, JpClassSignup.fields.paymentMedium)
		val price = new NullableDoubleFieldValue(self, JpClassSignup.fields.price)
		val voidCloseId = new NullableIntFieldValue(self, JpClassSignup.fields.voidCloseId)
		val signupNote = new NullableStringFieldValue(self, JpClassSignup.fields.signupNote)
		val ignoreIllegalSignup = new NullableBooleanFieldValue(self, JpClassSignup.fields.ignoreIllegalSignup)
		val staggerId = new NullableIntFieldValue(self, JpClassSignup.fields.staggerId)
		val parentSignupNote = new NullableStringFieldValue(self, JpClassSignup.fields.parentSignupNote)
		val sectionLookupId = new NullableIntFieldValue(self, JpClassSignup.fields.sectionLookupId)
		val sectionId = new NullableIntFieldValue(self, JpClassSignup.fields.sectionId)
	}
}

object JpClassSignup extends StorableObject[JpClassSignup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_SIGNUPS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		@NullableInDatabase
		val signupDatetime = new DateTimeDatabaseField(self, "SIGNUP_DATETIME")
		@NullableInDatabase
		val sequence = new DoubleDatabaseField(self, "SEQUENCE")
		@NullableInDatabase
		val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
		val groupId = new NullableIntDatabaseField(self, "GROUP_ID")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val isReserve = new NullableBooleanDatabaseField(self, "IS_RESERVE")
		val ccTransNum = new NullableStringDatabaseField(self, "CC_TRANS_NUM", 10)
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val signupNote = new NullableStringDatabaseField(self, "SIGNUP_NOTE", 4000)
		val ignoreIllegalSignup = new NullableBooleanDatabaseField(self, "IGNORE_ILLEGAL_SIGNUP")
		val staggerId = new NullableIntDatabaseField(self, "STAGGER_ID")
		val parentSignupNote = new NullableStringDatabaseField(self, "PARENT_SIGNUP_NOTE", 4000)
		val sectionLookupId = new NullableIntDatabaseField(self, "SECTION_LOOKUP_ID")
		val sectionId = new NullableIntDatabaseField(self, "SECTION_ID")
	}

	def primaryKey: IntDatabaseField = fields.signupId
}