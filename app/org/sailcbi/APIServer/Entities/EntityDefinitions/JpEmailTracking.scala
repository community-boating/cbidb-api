package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class JpEmailTracking extends StorableClass(JpEmailTracking) {
	override object values extends ValuesObject {
		val emailId = new IntFieldValue(self, JpEmailTracking.fields.emailId)
		val signupId = new NullableIntFieldValue(self, JpEmailTracking.fields.signupId)
		val emailFrom = new NullableStringFieldValue(self, JpEmailTracking.fields.emailFrom)
		val emailTo = new NullableStringFieldValue(self, JpEmailTracking.fields.emailTo)
		val emailSubj = new NullableStringFieldValue(self, JpEmailTracking.fields.emailSubj)
		val emailPlainBody = new NullableStringFieldValue(self, JpEmailTracking.fields.emailPlainBody)
		val emailHtmlBody = new NullableStringFieldValue(self, JpEmailTracking.fields.emailHtmlBody)
		val sendDatetime = new NullableDateTimeFieldValue(self, JpEmailTracking.fields.sendDatetime)
		val emailCc = new NullableStringFieldValue(self, JpEmailTracking.fields.emailCc)
		val emailBcc = new NullableStringFieldValue(self, JpEmailTracking.fields.emailBcc)
		val createdOn = new NullableDateTimeFieldValue(self, JpEmailTracking.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpEmailTracking.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, JpEmailTracking.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpEmailTracking.fields.updatedBy)
	}
}

object JpEmailTracking extends StorableObject[JpEmailTracking] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_EMAIL_TRACKING"

	object fields extends FieldsObject {
		val emailId = new IntDatabaseField(self, "EMAIL_ID")
		val signupId = new NullableIntDatabaseField(self, "SIGNUP_ID")
		val emailFrom = new NullableStringDatabaseField(self, "EMAIL_FROM", 200)
		val emailTo = new NullableStringDatabaseField(self, "EMAIL_TO", 200)
		val emailSubj = new NullableStringDatabaseField(self, "EMAIL_SUBJ", 500)
		val emailPlainBody = new NullableStringDatabaseField(self, "EMAIL_PLAIN_BODY", 4000)
		val emailHtmlBody = new NullableStringDatabaseField(self, "EMAIL_HTML_BODY", 4000)
		val sendDatetime = new NullableDateTimeDatabaseField(self, "SEND_DATETIME")
		val emailCc = new NullableStringDatabaseField(self, "EMAIL_CC", 200)
		val emailBcc = new NullableStringDatabaseField(self, "EMAIL_BCC", 200)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.emailId
}