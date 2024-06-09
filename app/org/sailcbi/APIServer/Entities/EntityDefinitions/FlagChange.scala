package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class FlagChange extends StorableClass(FlagChange) {
	override object values extends ValuesObject {
		val changeId = new IntFieldValue(self, FlagChange.fields.changeId)
		val flag = new StringFieldValue(self, FlagChange.fields.flag)
		val changeDatetime = new DateTimeFieldValue(self, FlagChange.fields.changeDatetime)
		val createdOn = new NullableDateTimeFieldValue(self, FlagChange.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FlagChange.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, FlagChange.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FlagChange.fields.updatedBy)
		val notificationsSent = new NullableIntFieldValue(self, FlagChange.fields.notificationsSent)
	}
}

object FlagChange extends StorableObject[FlagChange] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FLAG_CHANGES"

	object fields extends FieldsObject {
		val changeId = new IntDatabaseField(self, "CHANGE_ID")
		val flag = new StringDatabaseField(self, "FLAG", 1)
		val changeDatetime = new DateTimeDatabaseField(self, "CHANGE_DATETIME")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val notificationsSent = new NullableIntDatabaseField(self, "NOTIFICATIONS_SENT")
	}

	def primaryKey: IntDatabaseField = fields.changeId
}