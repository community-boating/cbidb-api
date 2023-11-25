package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassSession extends StorableClass(JpClassSession) {

	override object calculations extends CalculationsObject {
		val weekAlias = new Initializable[Option[String]]
	}
	
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
	}

	override object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, JpClassSession.fields.sessionId)
		val instanceId = new IntFieldValue(self, JpClassSession.fields.instanceId)
		val sessionDatetime = new DateTimeFieldValue(self, JpClassSession.fields.sessionDatetime)
		val createdOn = new DateTimeFieldValue(self, JpClassSession.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassSession.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpClassSession.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassSession.fields.updatedBy)
		val lengthOverride = new NullableDoubleFieldValue(self, JpClassSession.fields.lengthOverride)
	}
}

object JpClassSession extends StorableObject[JpClassSession] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_CLASS_SESSIONS"

	object fields extends FieldsObject {
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDatetime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val lengthOverride = new NullableDoubleDatabaseField(self, "LENGTH_OVERRIDE")
	}

	def primaryKey: IntDatabaseField = fields.sessionId
}