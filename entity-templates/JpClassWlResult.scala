package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassWlResult extends StorableClass(JpClassWlResult) {
	object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassWlResult.fields.signupId)
		val foVmDatetime = new NullableLocalDateTimeFieldValue(self, JpClassWlResult.fields.foVmDatetime)
		val wlResult = new BooleanFIeldValue(self, JpClassWlResult.fields.wlResult)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassWlResult.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassWlResult.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassWlResult.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassWlResult.fields.updatedBy)
		val offerExpDatetime = new LocalDateTimeFieldValue(self, JpClassWlResult.fields.offerExpDatetime)
		val foAlertDatetime = new LocalDateTimeFieldValue(self, JpClassWlResult.fields.foAlertDatetime)
		val permitOvercrowd = new NullableBooleanFIeldValue(self, JpClassWlResult.fields.permitOvercrowd)
		val preapprovedExp = new NullableLocalDateTimeFieldValue(self, JpClassWlResult.fields.preapprovedExp)
	}
}

object JpClassWlResult extends StorableObject[JpClassWlResult] {
	val entityName: String = "JP_CLASS_WL_RESULTS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val foVmDatetime = new NullableLocalDateTimeDatabaseField(self, "FO_VM_DATETIME")
		val wlResult = new BooleanDatabaseField(self, "WL_RESULT")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val offerExpDatetime = new LocalDateTimeDatabaseField(self, "OFFER_EXP_DATETIME")
		val foAlertDatetime = new LocalDateTimeDatabaseField(self, "FO_ALERT_DATETIME")
		val permitOvercrowd = new NullableBooleanDatabaseField(self, "PERMIT_OVERCROWD")
		val preapprovedExp = new NullableLocalDateTimeDatabaseField(self, "PREAPPROVED_EXP")
	}

	def primaryKey: IntDatabaseField = fields.signupId
}