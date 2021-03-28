package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.CbiUtil.Initializable
import com.coleji.framework.Storable.FieldValues.{DateTimeFieldValue, IntFieldValue}
import com.coleji.framework.Storable.Fields.{DateTimeDatabaseField, IntDatabaseField}
import com.coleji.framework.Storable.{FieldsObject, ReferencesObject, StorableClass, StorableObject, ValuesObject}
import com.coleji.framework.Storable._

class ApClassSession extends StorableClass {
	this.setCompanion(ApClassSession)

	object references extends ReferencesObject {
		var apClassInstance = new Initializable[ApClassInstance]
	}

	object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, ApClassSession.fields.sessionId)
		val instanceId = new IntFieldValue(self, ApClassSession.fields.instanceId)
		val sessionDateTime = new DateTimeFieldValue(self, ApClassSession.fields.sessionDateTime)
	}

	override val valuesList = List(
		values.sessionId,
		values.instanceId,
		values.sessionDateTime
	)
}

object ApClassSession extends StorableObject[ApClassSession] {
	val entityName: String = "AP_CLASS_SESSIONS"

	object fields extends FieldsObject {
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.sessionId
}