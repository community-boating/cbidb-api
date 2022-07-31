package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{DateTimeFieldValue, DoubleFieldValue, IntFieldValue, NullableDateTimeFieldValue, NullableIntFieldValue}
import com.coleji.neptune.Storable.Fields.{DateTimeDatabaseField, DoubleDatabaseField, IntDatabaseField, NullableDateTimeDatabaseField, NullableIntDatabaseField}
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassSession extends StorableClass(ApClassSession) {
	override object references extends ReferencesObject {
		val apClassInstance = new Initializable[ApClassInstance]
	}

	object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, ApClassSession.fields.sessionId)
		val instanceId = new IntFieldValue(self, ApClassSession.fields.instanceId)
		val sessionDateTime = new DateTimeFieldValue(self, ApClassSession.fields.sessionDateTime)
		val headcount = new NullableIntFieldValue(self, ApClassSession.fields.headcount)
		val cancelledDateTime = new NullableDateTimeFieldValue(self, ApClassSession.fields.cancelledDateTime)
		val sessionLength = new DoubleFieldValue(self, ApClassSession.fields.sessionLength)
	}
}

object ApClassSession extends StorableObject[ApClassSession] {
	val entityName: String = "AP_CLASS_SESSIONS"

	override val useRuntimeFieldnamesForJson: Boolean = true

	object fields extends FieldsObject {
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
		val headcount = new NullableIntDatabaseField(self, "HEADCOUNT")
		val cancelledDateTime = new NullableDateTimeDatabaseField(self, "CANCELLED_DATETIME")
		val sessionLength = new DoubleDatabaseField(self, "SESSION_LENGTH")
	}

	def primaryKey: IntDatabaseField = fields.sessionId
}