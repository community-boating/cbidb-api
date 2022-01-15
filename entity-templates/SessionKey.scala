package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SessionKey extends StorableClass(SessionKey) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, SessionKey.fields.rowId)
		val apexSession = new NullableStringFieldValue(self, SessionKey.fields.apexSession)
		val remoteKey = new NullableStringFieldValue(self, SessionKey.fields.remoteKey)
		val username = new NullableStringFieldValue(self, SessionKey.fields.username)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SessionKey.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SessionKey.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, SessionKey.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SessionKey.fields.updatedBy)
	}
}

object SessionKey extends StorableObject[SessionKey] {
	val entityName: String = "SESSION_KEYS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val apexSession = new NullableStringDatabaseField(self, "APEX_SESSION", 100)
		val remoteKey = new NullableStringDatabaseField(self, "REMOTE_KEY", 100)
		val username = new NullableStringDatabaseField(self, "USERNAME", 50)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}