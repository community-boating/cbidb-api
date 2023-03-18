package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{BooleanFieldValue, IntFieldValue, NullableDateTimeFieldValue, StringFieldValue}
import com.coleji.neptune.Storable.Fields.{BooleanDatabaseField, IntDatabaseField, NullableDateTimeDatabaseField, StringDatabaseField}
import com.coleji.neptune.Storable.{FieldsObject, StorableClass, StorableObject, ValuesObject}

class ApClassWaitlistResult extends StorableClass(ApClassWaitlistResult) {
	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassWaitlistResult.fields.signupId)
		val foVmDatetime = new NullableDateTimeFieldValue(self, ApClassWaitlistResult.fields.foVmDatetime)
		val wlResult = new StringFieldValue(self, ApClassWaitlistResult.fields.wlResult)
		val offerExpDatetime = new NullableDateTimeFieldValue(self, ApClassWaitlistResult.fields.offerExpDatetime)
		val foAlertDatetime = new NullableDateTimeFieldValue(self, ApClassWaitlistResult.fields.foAlertDatetime)
		val permitOvercrowd = new BooleanFieldValue(self, ApClassWaitlistResult.fields.permitOvercrowd)
	}
}

object ApClassWaitlistResult extends StorableObject[ApClassWaitlistResult] {
	val entityName: String = "AP_CLASS_WL_RESULTS"

	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val foVmDatetime = new NullableDateTimeDatabaseField(self, "FO_VM_DATETIME")
		val wlResult = new StringDatabaseField(self, "WL_RESULT", 1)
		val offerExpDatetime = new NullableDateTimeDatabaseField(self, "OFFER_EXP_DATETIME")
		val foAlertDatetime = new NullableDateTimeDatabaseField(self, "FO_ALERT_DATETIME")
		val permitOvercrowd = new BooleanDatabaseField(self, "PERMIT_OVERCROWD", true)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}
