package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class ApClassWaitlistResult extends StorableClass(ApClassWaitlistResult) {
	override object values extends ValuesObject {
		val signupId = new IntFieldValue(self, ApClassWaitlistResult.fields.signupId)
		val foVmDatetime = new NullableDateTimeFieldValue(self, ApClassWaitlistResult.fields.foVmDatetime)
		val wlResult = new StringFieldValue(self, ApClassWaitlistResult.fields.wlResult)
		val offerExpDatetime = new DateTimeFieldValue(self, ApClassWaitlistResult.fields.offerExpDatetime)
		val foAlertDatetime = new DateTimeFieldValue(self, ApClassWaitlistResult.fields.foAlertDatetime)
		val permitOvercrowd = new BooleanFieldValue(self, ApClassWaitlistResult.fields.permitOvercrowd)
		val createdOn = new DateTimeFieldValue(self, ApClassWaitlistResult.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassWaitlistResult.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ApClassWaitlistResult.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassWaitlistResult.fields.updatedBy)
	}
}

object ApClassWaitlistResult extends StorableObject[ApClassWaitlistResult] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_WL_RESULTS"

	object fields extends FieldsObject {
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val foVmDatetime = new NullableDateTimeDatabaseField(self, "FO_VM_DATETIME")
		val wlResult = new StringDatabaseField(self, "WL_RESULT", 1)
		@NullableInDatabase
		val offerExpDatetime = new DateTimeDatabaseField(self, "OFFER_EXP_DATETIME")
		@NullableInDatabase
		val foAlertDatetime = new DateTimeDatabaseField(self, "FO_ALERT_DATETIME")
		@NullableInDatabase
		val permitOvercrowd = new BooleanDatabaseField(self, "PERMIT_OVERCROWD", true)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.signupId
}