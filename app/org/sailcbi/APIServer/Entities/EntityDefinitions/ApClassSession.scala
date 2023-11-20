package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassSession extends StorableClass(ApClassSession) {
	override object references extends ReferencesObject {
		val apClassInstance = new Initializable[ApClassInstance]
	}

	override object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, ApClassSession.fields.sessionId)
		val instanceId = new IntFieldValue(self, ApClassSession.fields.instanceId)
		val sessionDatetime = new DateTimeFieldValue(self, ApClassSession.fields.sessionDatetime)
		val isMakeup = new NullableBooleanFieldValue(self, ApClassSession.fields.isMakeup)
		val createdOn = new DateTimeFieldValue(self, ApClassSession.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassSession.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, ApClassSession.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassSession.fields.updatedBy)
		val cancelledDatetime = new NullableDateTimeFieldValue(self, ApClassSession.fields.cancelledDatetime)
		val headcount = new NullableDoubleFieldValue(self, ApClassSession.fields.headcount)
		val sessionLength = new DoubleFieldValue(self, ApClassSession.fields.sessionLength)
	}
}

object ApClassSession extends StorableObject[ApClassSession] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "AP_CLASS_SESSIONS"

	object fields extends FieldsObject {
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDatetime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
		val isMakeup = new NullableBooleanDatabaseField(self, "IS_MAKEUP")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val cancelledDatetime = new NullableDateTimeDatabaseField(self, "CANCELLED_DATETIME")
		val headcount = new NullableDoubleDatabaseField(self, "HEADCOUNT")
		val sessionLength = new DoubleDatabaseField(self, "SESSION_LENGTH")
	}

	def primaryKey: IntDatabaseField = fields.sessionId
}