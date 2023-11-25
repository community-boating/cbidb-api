package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class ApClassSignup extends StorableClass(ApClassSignup) {
	override object references extends ReferencesObject {
		val apClassInstance = new Initializable[ApClassInstance]
		val person = new Initializable[Person]
		val apClassWaitlistResult = new Initializable[Option[ApClassWaitlistResult]]
	}

	override object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassSignup.fields.signupId)
		val instanceId = new IntFieldValue(self, ApClassSignup.fields.instanceId)
		val personId = new IntFieldValue(self, ApClassSignup.fields.personId)
		val orderId = new NullableIntFieldValue(self, ApClassSignup.fields.orderId)
		val signupDatetime = new DateTimeFieldValue(self, ApClassSignup.fields.signupDatetime)
		val signupType = new StringFieldValue(self, ApClassSignup.fields.signupType)
		val createdOn = new DateTimeFieldValue(self, ApClassSignup.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassSignup.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ApClassSignup.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassSignup.fields.updatedBy)
		val ccTransNum = new NullableStringFieldValue(self, ApClassSignup.fields.ccTransNum)
		val closeId = new NullableIntFieldValue(self, ApClassSignup.fields.closeId)
		val discountInstanceId = new NullableIntFieldValue(self, ApClassSignup.fields.discountInstanceId)
		val paymentMedium = new NullableStringFieldValue(self, ApClassSignup.fields.paymentMedium)
		val price = new NullableDoubleFieldValue(self, ApClassSignup.fields.price)
		val sequence = new IntFieldValue(self, ApClassSignup.fields.sequence)
		val voidCloseId = new NullableIntFieldValue(self, ApClassSignup.fields.voidCloseId)
		val paymentLocation = new NullableStringFieldValue(self, ApClassSignup.fields.paymentLocation)
		val signupNote = new NullableStringFieldValue(self, ApClassSignup.fields.signupNote)
		val voidedOnline = new BooleanFieldValue(self, ApClassSignup.fields.voidedOnline)
	}
}

object ApClassSignup extends StorableObject[ApClassSignup] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_SIGNUPS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
		val signupDatetime = new DateTimeDatabaseField(self, "SIGNUP_DATETIME")
		val signupType = new StringDatabaseField(self, "SIGNUP_TYPE", 1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val ccTransNum = new NullableStringDatabaseField(self, "CC_TRANS_NUM", 50)
		val closeId = new NullableIntDatabaseField(self, "CLOSE_ID")
		val discountInstanceId = new NullableIntDatabaseField(self, "DISCOUNT_INSTANCE_ID")
		val paymentMedium = new NullableStringDatabaseField(self, "PAYMENT_MEDIUM", 50)
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val sequence = new IntDatabaseField(self, "SEQUENCE")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val paymentLocation = new NullableStringDatabaseField(self, "PAYMENT_LOCATION", 50)
		val signupNote = new NullableStringDatabaseField(self, "SIGNUP_NOTE", 4000)
		@NullableInDatabase
		val voidedOnline = new BooleanDatabaseField(self, "VOIDED_ONLINE", true)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}