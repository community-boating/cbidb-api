package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class EmailContent extends StorableClass(EmailContent) {
	override object values extends ValuesObject {
		val contentId = new IntFieldValue(self, EmailContent.fields.contentId)
		val fromAddr = new StringFieldValue(self, EmailContent.fields.fromAddr)
		val ccAddr = new NullableStringFieldValue(self, EmailContent.fields.ccAddr)
		val bccAddr = new NullableStringFieldValue(self, EmailContent.fields.bccAddr)
		val subject = new StringFieldValue(self, EmailContent.fields.subject)
		val plainBody = new NullableStringFieldValue(self, EmailContent.fields.plainBody)
		val htmlBody = new NullableStringFieldValue(self, EmailContent.fields.htmlBody)
		val description = new NullableStringFieldValue(self, EmailContent.fields.description)
		val title = new StringFieldValue(self, EmailContent.fields.title)
		val createdOn = new DateTimeFieldValue(self, EmailContent.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EmailContent.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, EmailContent.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, EmailContent.fields.updatedBy)
		val emailGroupId = new IntFieldValue(self, EmailContent.fields.emailGroupId)
		val recGroupId = new NullableIntFieldValue(self, EmailContent.fields.recGroupId)
		val htmlBodyClob = new NullableStringFieldValue(self, EmailContent.fields.htmlBodyClob)
		val fromAddrName = new NullableStringFieldValue(self, EmailContent.fields.fromAddrName)
		val fromAddrOnly = new NullableStringFieldValue(self, EmailContent.fields.fromAddrOnly)
		val optOutGrpId = new NullableIntFieldValue(self, EmailContent.fields.optOutGrpId)
		val replyTo = new NullableStringFieldValue(self, EmailContent.fields.replyTo)
		val hidden = new NullableBooleanFieldValue(self, EmailContent.fields.hidden)
	}
}

object EmailContent extends StorableObject[EmailContent] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EMAIL_CONTENT"

	object fields extends FieldsObject {
		val contentId = new IntDatabaseField(self, "CONTENT_ID")
		@NullableInDatabase
		val fromAddr = new StringDatabaseField(self, "FROM_ADDR", 100)
		val ccAddr = new NullableStringDatabaseField(self, "CC_ADDR", 500)
		val bccAddr = new NullableStringDatabaseField(self, "BCC_ADDR", 500)
		@NullableInDatabase
		val subject = new StringDatabaseField(self, "SUBJECT", 200)
		val plainBody = new NullableStringDatabaseField(self, "PLAIN_BODY", 4000)
		val htmlBody = new NullableStringDatabaseField(self, "HTML_BODY", 4000)
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 4000)
		val title = new StringDatabaseField(self, "TITLE", 50)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		@NullableInDatabase
		val emailGroupId = new IntDatabaseField(self, "EMAIL_GROUP_ID")
		val recGroupId = new NullableIntDatabaseField(self, "REC_GROUP_ID")
		val htmlBodyClob = new NullableStringDatabaseField(self, "HTML_BODY_CLOB", -1)
		val fromAddrName = new NullableStringDatabaseField(self, "FROM_ADDR_NAME", 100)
		val fromAddrOnly = new NullableStringDatabaseField(self, "FROM_ADDR_ONLY", 100)
		val optOutGrpId = new NullableIntDatabaseField(self, "OPT_OUT_GRP_ID")
		val replyTo = new NullableStringDatabaseField(self, "REPLY_TO", 100)
		val hidden = new NullableBooleanDatabaseField(self, "HIDDEN")
	}

	def primaryKey: IntDatabaseField = fields.contentId
}