package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class ApClassAttendance extends StorableClass(ApClassAttendance) {
	override object values extends ValuesObject {
		val attendId = new IntFieldValue(self, ApClassAttendance.fields.attendId)
		val sessionId = new IntFieldValue(self, ApClassAttendance.fields.sessionId)
		val signupId = new IntFieldValue(self, ApClassAttendance.fields.signupId)
		val attend = new NullableStringFieldValue(self, ApClassAttendance.fields.attend)
		val createdOn = new DateTimeFieldValue(self, ApClassAttendance.fields.createdOn)
		val createdBy = new StringFieldValue(self, ApClassAttendance.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ApClassAttendance.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, ApClassAttendance.fields.updatedBy)
	}
}

object ApClassAttendance extends StorableObject[ApClassAttendance] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_ATTENDANCE"

	object fields extends FieldsObject {
		val attendId = new IntDatabaseField(self, "ATTEND_ID")
		@NullableInDatabase
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		@NullableInDatabase
		val signupId = new IntDatabaseField(self, "SIGNUP_ID")
		val attend = new NullableStringDatabaseField(self, "ATTEND", 1)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.attendId
}