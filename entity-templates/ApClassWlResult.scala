package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassWlResult extends StorableClass(ApClassWlResult) {
	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassWlResult.fields.signupId)
		val foVmDatetime = new NullableLocalDateTimeFieldValue(self, ApClassWlResult.fields.foVmDatetime)
		val wlResult = new NullableBooleanFIeldValue(self, ApClassWlResult.fields.wlResult)
		val offerExpDatetime = new NullableLocalDateTimeFieldValue(self, ApClassWlResult.fields.offerExpDatetime)
		val foAlertDatetime = new NullableLocalDateTimeFieldValue(self, ApClassWlResult.fields.foAlertDatetime)
		val permitOvercrowd = new NullableBooleanFIeldValue(self, ApClassWlResult.fields.permitOvercrowd)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassWlResult.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassWlResult.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassWlResult.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassWlResult.fields.updatedBy)
	}
}

object ApClassWlResult extends StorableObject[ApClassWlResult] {
	val entityName: String = "AP_CLASS_WL_RESULTS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val foVmDatetime = new NullableLocalDateTimeDatabaseField(self, "FO_VM_DATETIME")
		val wlResult = new NullableBooleanDatabaseField(self, "WL_RESULT")
		val offerExpDatetime = new NullableLocalDateTimeDatabaseField(self, "OFFER_EXP_DATETIME")
		val foAlertDatetime = new NullableLocalDateTimeDatabaseField(self, "FO_ALERT_DATETIME")
		val permitOvercrowd = new NullableBooleanDatabaseField(self, "PERMIT_OVERCROWD")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}