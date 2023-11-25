package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class SessionBlacklist extends StorableClass(SessionBlacklist) {
	override object values extends ValuesObject {
		val blacklistId = new IntFieldValue(self, SessionBlacklist.fields.blacklistId)
		val sessionId = new StringFieldValue(self, SessionBlacklist.fields.sessionId)
		val blacklistDatetime = new DateTimeFieldValue(self, SessionBlacklist.fields.blacklistDatetime)
		val createdOn = new NullableDateTimeFieldValue(self, SessionBlacklist.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, SessionBlacklist.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, SessionBlacklist.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, SessionBlacklist.fields.updatedBy)
	}
}

object SessionBlacklist extends StorableObject[SessionBlacklist] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SESSION_BLACKLIST"

	object fields extends FieldsObject {
		val blacklistId = new IntDatabaseField(self, "BLACKLIST_ID")
		@NullableInDatabase
		val sessionId = new StringDatabaseField(self, "SESSION_ID", 50)
		@NullableInDatabase
		val blacklistDatetime = new DateTimeDatabaseField(self, "BLACKLIST_DATETIME")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.blacklistId
}