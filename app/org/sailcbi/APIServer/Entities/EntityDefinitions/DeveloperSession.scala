package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class DeveloperSession extends StorableClass(DeveloperSession) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, DeveloperSession.fields.rowId)
		val userName = new StringFieldValue(self, DeveloperSession.fields.userName)
		val sessionId = new IntFieldValue(self, DeveloperSession.fields.sessionId)
		val viewDate = new DateTimeFieldValue(self, DeveloperSession.fields.viewDate)
	}
}

object DeveloperSession extends StorableObject[DeveloperSession] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DEVELOPER_SESSIONS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val userName = new StringDatabaseField(self, "USER_NAME", 50)
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val viewDate = new DateTimeDatabaseField(self, "VIEW_DATE")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}