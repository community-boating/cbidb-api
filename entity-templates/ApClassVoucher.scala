package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassVoucher extends StorableClass(ApClassVoucher) {
	object values extends ValuesObject {
		val voucherId = new IntFieldValue(self, ApClassVoucher.fields.voucherId)
		val personId = new IntFieldValue(self, ApClassVoucher.fields.personId)
		val season = new DoubleFieldValue(self, ApClassVoucher.fields.season)
		val value = new DoubleFieldValue(self, ApClassVoucher.fields.value)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassVoucher.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassVoucher.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassVoucher.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassVoucher.fields.updatedBy)
		val closeId = new IntFieldValue(self, ApClassVoucher.fields.closeId)
		val voidCloseId = new NullableIntFieldValue(self, ApClassVoucher.fields.voidCloseId)
		val signupId = new NullableIntFieldValue(self, ApClassVoucher.fields.signupId)
		val createdOnline = new NullableBooleanFIeldValue(self, ApClassVoucher.fields.createdOnline)
	}
}

object ApClassVoucher extends StorableObject[ApClassVoucher] {
	val entityName: String = "AP_CLASS_VOUCHERS"

	object fields extends FieldsObject {
		val voucherId = new IntDatabaseField(self, "VOUCHER_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val value = new DoubleDatabaseField(self, "VALUE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val voidCloseId = new NullableIntDatabaseField(self, "VOID_CLOSE_ID")
		val signupId = new NullableIntDatabaseField(self, "SIGNUP_ID")
		val createdOnline = new NullableBooleanDatabaseField(self, "CREATED_ONLINE")
	}

	def primaryKey: IntDatabaseField = fields.voucherId
}