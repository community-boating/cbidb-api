package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class JpClassAttendance extends StorableClass(JpClassAttendance) {
	override object values extends ValuesObject {
		val attendId = new IntFieldValue(self, JpClassAttendance.fields.attendId)
		val sessionId = new IntFieldValue(self, JpClassAttendance.fields.sessionId)
		val signupId = new IntFieldValue(self, JpClassAttendance.fields.signupId)
		val attend = new NullableStringFieldValue(self, JpClassAttendance.fields.attend)
		val createdOn = new NullableDateTimeFieldValue(self, JpClassAttendance.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassAttendance.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, JpClassAttendance.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassAttendance.fields.updatedBy)
	}
}

object JpClassAttendance extends StorableObject[JpClassAttendance] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_ATTENDANCE"

	object fields extends FieldsObject {
		val attendId = new IntDatabaseField(self, "ATTEND_ID")
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val attend = new NullableStringDatabaseField(self, "ATTEND", 1)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.attendId
}