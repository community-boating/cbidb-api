package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class SessionKey extends StorableClass(SessionKey) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, SessionKey.fields.rowId)
		val apexSession = new StringFieldValue(self, SessionKey.fields.apexSession)
		val remoteKey = new StringFieldValue(self, SessionKey.fields.remoteKey)
		val username = new StringFieldValue(self, SessionKey.fields.username)
		val createdOn = new DateTimeFieldValue(self, SessionKey.fields.createdOn)
		val createdBy = new StringFieldValue(self, SessionKey.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, SessionKey.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, SessionKey.fields.updatedBy)
	}
}

object SessionKey extends StorableObject[SessionKey] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "SESSION_KEYS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		@NullableInDatabase
		val apexSession = new StringDatabaseField(self, "APEX_SESSION", 100)
		@NullableInDatabase
		val remoteKey = new StringDatabaseField(self, "REMOTE_KEY", 100)
		@NullableInDatabase
		val username = new StringDatabaseField(self, "USERNAME", 50)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}