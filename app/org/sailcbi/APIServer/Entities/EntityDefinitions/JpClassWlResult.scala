package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassWlResult extends StorableClass(JpClassWlResult) {
	override object values extends ValuesObject {
		val signupId = new IntFieldValue(self, JpClassWlResult.fields.signupId)
		val foVmDatetime = new NullableDateTimeFieldValue(self, JpClassWlResult.fields.foVmDatetime)
		val wlResult = new StringFieldValue(self, JpClassWlResult.fields.wlResult)
		val createdOn = new DateTimeFieldValue(self, JpClassWlResult.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassWlResult.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassWlResult.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassWlResult.fields.updatedBy)
		val foAlertDatetime = new DateTimeFieldValue(self, JpClassWlResult.fields.foAlertDatetime)
		val offerExpDatetime = new DateTimeFieldValue(self, JpClassWlResult.fields.offerExpDatetime)
		val permitOvercrowd = new NullableBooleanFieldValue(self, JpClassWlResult.fields.permitOvercrowd)
		val preapprovedExp = new NullableDateTimeFieldValue(self, JpClassWlResult.fields.preapprovedExp)
	}
}

object JpClassWlResult extends StorableObject[JpClassWlResult] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_WL_RESULTS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val foVmDatetime = new NullableDateTimeDatabaseField(self, "FO_VM_DATETIME")
		val wlResult = new StringDatabaseField(self, "WL_RESULT", 1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val foAlertDatetime = new DateTimeDatabaseField(self, "FO_ALERT_DATETIME")
		val offerExpDatetime = new DateTimeDatabaseField(self, "OFFER_EXP_DATETIME")
		val permitOvercrowd = new NullableBooleanDatabaseField(self, "PERMIT_OVERCROWD")
		val preapprovedExp = new NullableDateTimeDatabaseField(self, "PREAPPROVED_EXP")
	}

	def primaryKey: IntDatabaseField = fields.signupId
}