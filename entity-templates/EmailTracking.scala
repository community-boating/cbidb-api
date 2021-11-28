package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EmailTracking extends StorableClass(EmailTracking) {
	object values extends ValuesObject {
		val emailId = new IntFieldValue(self, EmailTracking.fields.emailId)
		val emailFrom = new NullableStringFieldValue(self, EmailTracking.fields.emailFrom)
		val emailTo = new NullableStringFieldValue(self, EmailTracking.fields.emailTo)
		val emailSubj = new NullableStringFieldValue(self, EmailTracking.fields.emailSubj)
		val emailPlainBody = new NullableStringFieldValue(self, EmailTracking.fields.emailPlainBody)
		val emailHtmlBody = new NullableStringFieldValue(self, EmailTracking.fields.emailHtmlBody)
		val sendDatetime = new NullableLocalDateTimeFieldValue(self, EmailTracking.fields.sendDatetime)
		val emailCc = new NullableStringFieldValue(self, EmailTracking.fields.emailCc)
		val emailBcc = new NullableStringFieldValue(self, EmailTracking.fields.emailBcc)
		val createdOn = new NullableLocalDateTimeFieldValue(self, EmailTracking.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EmailTracking.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, EmailTracking.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, EmailTracking.fields.updatedBy)
		val status = new NullableBooleanFIeldValue(self, EmailTracking.fields.status)
		val sendgridJson = new NullableUnknownFieldType(self, EmailTracking.fields.sendgridJson)
		val retryCt = new NullableDoubleFieldValue(self, EmailTracking.fields.retryCt)
		val emailFromAddrName = new NullableStringFieldValue(self, EmailTracking.fields.emailFromAddrName)
		val emailFromAddrOnly = new NullableStringFieldValue(self, EmailTracking.fields.emailFromAddrOnly)
		val optOutGrpId = new NullableIntFieldValue(self, EmailTracking.fields.optOutGrpId)
		val sendgridResp = new NullableStringFieldValue(self, EmailTracking.fields.sendgridResp)
	}
}

object EmailTracking extends StorableObject[EmailTracking] {
	val entityName: String = "EMAIL_TRACKING"

	object fields extends FieldsObject {
		val emailId = new IntDatabaseField(self, "EMAIL_ID")
		val emailFrom = new NullableStringDatabaseField(self, "EMAIL_FROM", 200)
		val emailTo = new NullableStringDatabaseField(self, "EMAIL_TO", 200)
		val emailSubj = new NullableStringDatabaseField(self, "EMAIL_SUBJ", 500)
		val emailPlainBody = new NullableStringDatabaseField(self, "EMAIL_PLAIN_BODY", 4000)
		val emailHtmlBody = new NullableStringDatabaseField(self, "EMAIL_HTML_BODY", 4000)
		val sendDatetime = new NullableLocalDateTimeDatabaseField(self, "SEND_DATETIME")
		val emailCc = new NullableStringDatabaseField(self, "EMAIL_CC", 200)
		val emailBcc = new NullableStringDatabaseField(self, "EMAIL_BCC", 200)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val status = new NullableBooleanDatabaseField(self, "STATUS")
		val sendgridJson = new NullableUnknownFieldType(self, "SENDGRID_JSON")
		val retryCt = new NullableDoubleDatabaseField(self, "RETRY_CT")
		val emailFromAddrName = new NullableStringDatabaseField(self, "EMAIL_FROM_ADDR_NAME", 100)
		val emailFromAddrOnly = new NullableStringDatabaseField(self, "EMAIL_FROM_ADDR_ONLY", 100)
		val optOutGrpId = new NullableIntDatabaseField(self, "OPT_OUT_GRP_ID")
		val sendgridResp = new NullableStringDatabaseField(self, "SENDGRID_RESP", 20)
	}

	def primaryKey: IntDatabaseField = fields.emailId
}