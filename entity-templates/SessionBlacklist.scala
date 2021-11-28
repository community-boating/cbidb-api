package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class SessionBlacklist extends StorableClass(SessionBlacklist) {
	object values extends ValuesObject {
		val blacklistId = new IntFieldValue(self, SessionBlacklist.fields.blacklistId)
		val sessionId = new NullableStringFieldValue(self, SessionBlacklist.fields.sessionId)
		val blacklistDatetime = new NullableLocalDateTimeFieldValue(self, SessionBlacklist.fields.blacklistDatetime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, SessionBlacklist.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SessionBlacklist.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, SessionBlacklist.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SessionBlacklist.fields.updatedBy)
	}
}

object SessionBlacklist extends StorableObject[SessionBlacklist] {
	val entityName: String = "SESSION_BLACKLIST"

	object fields extends FieldsObject {
		val blacklistId = new IntDatabaseField(self, "BLACKLIST_ID")
		val sessionId = new NullableStringDatabaseField(self, "SESSION_ID", 50)
		val blacklistDatetime = new NullableLocalDateTimeDatabaseField(self, "BLACKLIST_DATETIME")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.blacklistId
}