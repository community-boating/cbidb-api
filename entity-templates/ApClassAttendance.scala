package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassAttendance extends StorableClass(ApClassAttendance) {
	object values extends ValuesObject {
		val attendId = new IntFieldValue(self, ApClassAttendance.fields.attendId)
		val sessionId = new NullableIntFieldValue(self, ApClassAttendance.fields.sessionId)
		val signupId = new NullableIntFieldValue(self, ApClassAttendance.fields.signupId)
		val attend = new NullableBooleanFIeldValue(self, ApClassAttendance.fields.attend)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassAttendance.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassAttendance.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassAttendance.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassAttendance.fields.updatedBy)
	}
}

object ApClassAttendance extends StorableObject[ApClassAttendance] {
	val entityName: String = "AP_CLASS_ATTENDANCE"

	object fields extends FieldsObject {
		val attendId = new IntDatabaseField(self, "ATTEND_ID")
		val sessionId = new NullableIntDatabaseField(self, "SESSION_ID")
		val signupId = new NullableIntDatabaseField(self, "SIGNUP_ID")
		val attend = new NullableBooleanDatabaseField(self, "ATTEND")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.attendId
}