package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DeveloperSession extends StorableClass(DeveloperSession) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, DeveloperSession.fields.rowId)
		val userName = new StringFieldValue(self, DeveloperSession.fields.userName)
		val sessionId = new IntFieldValue(self, DeveloperSession.fields.sessionId)
		val viewDate = new LocalDateTimeFieldValue(self, DeveloperSession.fields.viewDate)
	}
}

object DeveloperSession extends StorableObject[DeveloperSession] {
	val entityName: String = "DEVELOPER_SESSIONS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val userName = new StringDatabaseField(self, "USER_NAME", 50)
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val viewDate = new LocalDateTimeDatabaseField(self, "VIEW_DATE")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}