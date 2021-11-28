package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Feedback extends StorableClass(Feedback) {
	object values extends ValuesObject {
		val feedbackId = new IntFieldValue(self, Feedback.fields.feedbackId)
		val appAlias = new NullableStringFieldValue(self, Feedback.fields.appAlias)
		val appPageId = new NullableIntFieldValue(self, Feedback.fields.appPageId)
		val description = new NullableUnknownFieldType(self, Feedback.fields.description)
		val userName = new NullableStringFieldValue(self, Feedback.fields.userName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Feedback.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Feedback.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Feedback.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Feedback.fields.updatedBy)
		val appId = new NullableIntFieldValue(self, Feedback.fields.appId)
	}
}

object Feedback extends StorableObject[Feedback] {
	val entityName: String = "FEEDBACK"

	object fields extends FieldsObject {
		val feedbackId = new IntDatabaseField(self, "FEEDBACK_ID")
		val appAlias = new NullableStringDatabaseField(self, "APP_ALIAS", 100)
		val appPageId = new NullableIntDatabaseField(self, "APP_PAGE_ID")
		val description = new NullableUnknownFieldType(self, "DESCRIPTION")
		val userName = new NullableStringDatabaseField(self, "USER_NAME", 20)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val appId = new NullableIntDatabaseField(self, "APP_ID")
	}

	def primaryKey: IntDatabaseField = fields.feedbackId
}