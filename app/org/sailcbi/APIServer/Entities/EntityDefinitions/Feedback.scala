package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Feedback extends StorableClass(Feedback) {
	override object values extends ValuesObject {
		val feedbackId = new IntFieldValue(self, Feedback.fields.feedbackId)
		val appAlias = new StringFieldValue(self, Feedback.fields.appAlias)
		val appPageId = new IntFieldValue(self, Feedback.fields.appPageId)
		val description = new NullableUnknownFieldType(self, Feedback.fields.description)
		val userName = new StringFieldValue(self, Feedback.fields.userName)
		val createdOn = new DateTimeFieldValue(self, Feedback.fields.createdOn)
		val createdBy = new StringFieldValue(self, Feedback.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Feedback.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, Feedback.fields.updatedBy)
		val appId = new IntFieldValue(self, Feedback.fields.appId)
	}
}

object Feedback extends StorableObject[Feedback] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FEEDBACK"

	object fields extends FieldsObject {
		val feedbackId = new IntDatabaseField(self, "FEEDBACK_ID")
		val appAlias = new StringDatabaseField(self, "APP_ALIAS", 100)
		val appPageId = new IntDatabaseField(self, "APP_PAGE_ID")
		val description = new NullableUnknownFieldType(self, "DESCRIPTION")
		val userName = new StringDatabaseField(self, "USER_NAME", 20)
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val appId = new IntDatabaseField(self, "APP_ID")
	}

	def primaryKey: IntDatabaseField = fields.feedbackId
}