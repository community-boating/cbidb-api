package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EmailContent extends StorableClass(EmailContent) {
	object values extends ValuesObject {
		val contentId = new IntFieldValue(self, EmailContent.fields.contentId)
		val fromAddr = new NullableStringFieldValue(self, EmailContent.fields.fromAddr)
		val ccAddr = new NullableStringFieldValue(self, EmailContent.fields.ccAddr)
		val bccAddr = new NullableStringFieldValue(self, EmailContent.fields.bccAddr)
		val subject = new NullableStringFieldValue(self, EmailContent.fields.subject)
		val plainBody = new NullableStringFieldValue(self, EmailContent.fields.plainBody)
		val htmlBody = new NullableStringFieldValue(self, EmailContent.fields.htmlBody)
		val description = new NullableStringFieldValue(self, EmailContent.fields.description)
		val title = new StringFieldValue(self, EmailContent.fields.title)
		val createdOn = new NullableLocalDateTimeFieldValue(self, EmailContent.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EmailContent.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, EmailContent.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, EmailContent.fields.updatedBy)
		val emailGroupId = new NullableIntFieldValue(self, EmailContent.fields.emailGroupId)
		val recGroupId = new NullableIntFieldValue(self, EmailContent.fields.recGroupId)
		val htmlBodyClob = new NullableUnknownFieldType(self, EmailContent.fields.htmlBodyClob)
		val fromAddrOnly = new NullableStringFieldValue(self, EmailContent.fields.fromAddrOnly)
		val fromAddrName = new NullableStringFieldValue(self, EmailContent.fields.fromAddrName)
		val optOutGrpId = new NullableIntFieldValue(self, EmailContent.fields.optOutGrpId)
		val replyTo = new NullableStringFieldValue(self, EmailContent.fields.replyTo)
		val hidden = new NullableBooleanFIeldValue(self, EmailContent.fields.hidden)
	}
}

object EmailContent extends StorableObject[EmailContent] {
	val entityName: String = "EMAIL_CONTENT"

	object fields extends FieldsObject {
		val contentId = new IntDatabaseField(self, "CONTENT_ID")
		val fromAddr = new NullableStringDatabaseField(self, "FROM_ADDR", 100)
		val ccAddr = new NullableStringDatabaseField(self, "CC_ADDR", 500)
		val bccAddr = new NullableStringDatabaseField(self, "BCC_ADDR", 500)
		val subject = new NullableStringDatabaseField(self, "SUBJECT", 200)
		val plainBody = new NullableStringDatabaseField(self, "PLAIN_BODY", 4000)
		val htmlBody = new NullableStringDatabaseField(self, "HTML_BODY", 4000)
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 4000)
		val title = new StringDatabaseField(self, "TITLE", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val emailGroupId = new NullableIntDatabaseField(self, "EMAIL_GROUP_ID")
		val recGroupId = new NullableIntDatabaseField(self, "REC_GROUP_ID")
		val htmlBodyClob = new NullableUnknownFieldType(self, "HTML_BODY_CLOB")
		val fromAddrOnly = new NullableStringDatabaseField(self, "FROM_ADDR_ONLY", 100)
		val fromAddrName = new NullableStringDatabaseField(self, "FROM_ADDR_NAME", 100)
		val optOutGrpId = new NullableIntDatabaseField(self, "OPT_OUT_GRP_ID")
		val replyTo = new NullableStringDatabaseField(self, "REPLY_TO", 100)
		val hidden = new NullableBooleanDatabaseField(self, "HIDDEN")
	}

	def primaryKey: IntDatabaseField = fields.contentId
}