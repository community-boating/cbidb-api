package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassSession extends StorableClass(ApClassSession) {
	object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, ApClassSession.fields.sessionId)
		val instanceId = new IntFieldValue(self, ApClassSession.fields.instanceId)
		val sessionDatetime = new LocalDateTimeFieldValue(self, ApClassSession.fields.sessionDatetime)
		val isMakeup = new NullableBooleanFIeldValue(self, ApClassSession.fields.isMakeup)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassSession.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassSession.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassSession.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassSession.fields.updatedBy)
		val headcount = new NullableDoubleFieldValue(self, ApClassSession.fields.headcount)
		val cancelledDatetime = new NullableLocalDateTimeFieldValue(self, ApClassSession.fields.cancelledDatetime)
		val sessionLength = new DoubleFieldValue(self, ApClassSession.fields.sessionLength)
	}
}

object ApClassSession extends StorableObject[ApClassSession] {
	val entityName: String = "AP_CLASS_SESSIONS"

	object fields extends FieldsObject {
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDatetime = new LocalDateTimeDatabaseField(self, "SESSION_DATETIME")
		val isMakeup = new NullableBooleanDatabaseField(self, "IS_MAKEUP")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val headcount = new NullableDoubleDatabaseField(self, "HEADCOUNT")
		val cancelledDatetime = new NullableLocalDateTimeDatabaseField(self, "CANCELLED_DATETIME")
		val sessionLength = new DoubleDatabaseField(self, "SESSION_LENGTH")
	}

	def primaryKey: IntDatabaseField = fields.sessionId
}