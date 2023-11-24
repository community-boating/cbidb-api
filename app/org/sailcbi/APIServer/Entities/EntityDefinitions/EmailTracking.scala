package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class EmailTracking extends StorableClass(EmailTracking) {
	override object values extends ValuesObject {
		val emailId = new IntFieldValue(self, EmailTracking.fields.emailId)
		val emailFrom = new StringFieldValue(self, EmailTracking.fields.emailFrom)
		val emailTo = new StringFieldValue(self, EmailTracking.fields.emailTo)
		val emailSubj = new StringFieldValue(self, EmailTracking.fields.emailSubj)
		val emailPlainBody = new NullableStringFieldValue(self, EmailTracking.fields.emailPlainBody)
		val emailHtmlBody = new NullableStringFieldValue(self, EmailTracking.fields.emailHtmlBody)
		val sendDatetime = new NullableDateTimeFieldValue(self, EmailTracking.fields.sendDatetime)
		val emailCc = new NullableStringFieldValue(self, EmailTracking.fields.emailCc)
		val emailBcc = new NullableStringFieldValue(self, EmailTracking.fields.emailBcc)
		val createdOn = new DateTimeFieldValue(self, EmailTracking.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EmailTracking.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, EmailTracking.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, EmailTracking.fields.updatedBy)
		val status = new NullableStringFieldValue(self, EmailTracking.fields.status)
		val sendgridJson = new NullableStringFieldValue(self, EmailTracking.fields.sendgridJson)
		val retryCt = new NullableDoubleFieldValue(self, EmailTracking.fields.retryCt)
		val emailFromAddrName = new NullableStringFieldValue(self, EmailTracking.fields.emailFromAddrName)
		val emailFromAddrOnly = new NullableStringFieldValue(self, EmailTracking.fields.emailFromAddrOnly)
		val optOutGrpId = new NullableIntFieldValue(self, EmailTracking.fields.optOutGrpId)
		val sendgridResp = new NullableStringFieldValue(self, EmailTracking.fields.sendgridResp)
	}
}

object EmailTracking extends StorableObject[EmailTracking] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EMAIL_TRACKING"

	object fields extends FieldsObject {
		val emailId = new IntDatabaseField(self, "EMAIL_ID")
		@NullableInDatabase
		val emailFrom = new StringDatabaseField(self, "EMAIL_FROM", 200)
		@NullableInDatabase
		val emailTo = new StringDatabaseField(self, "EMAIL_TO", 200)
		@NullableInDatabase
		val emailSubj = new StringDatabaseField(self, "EMAIL_SUBJ", 500)
		val emailPlainBody = new NullableStringDatabaseField(self, "EMAIL_PLAIN_BODY", 4000)
		val emailHtmlBody = new NullableStringDatabaseField(self, "EMAIL_HTML_BODY", 4000)
		val sendDatetime = new NullableDateTimeDatabaseField(self, "SEND_DATETIME")
		val emailCc = new NullableStringDatabaseField(self, "EMAIL_CC", 200)
		val emailBcc = new NullableStringDatabaseField(self, "EMAIL_BCC", 200)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val status = new NullableStringDatabaseField(self, "STATUS", 1)
		val sendgridJson = new NullableStringDatabaseField(self, "SENDGRID_JSON", -1)
		val retryCt = new NullableDoubleDatabaseField(self, "RETRY_CT")
		val emailFromAddrName = new NullableStringDatabaseField(self, "EMAIL_FROM_ADDR_NAME", 100)
		val emailFromAddrOnly = new NullableStringDatabaseField(self, "EMAIL_FROM_ADDR_ONLY", 100)
		val optOutGrpId = new NullableIntDatabaseField(self, "OPT_OUT_GRP_ID")
		val sendgridResp = new NullableStringDatabaseField(self, "SENDGRID_RESP", 20)
	}

	def primaryKey: IntDatabaseField = fields.emailId
}