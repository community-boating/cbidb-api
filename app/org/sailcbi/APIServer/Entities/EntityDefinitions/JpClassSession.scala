package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.FieldValues.{DateTimeFieldValue, IntFieldValue, NullableDoubleFieldValue}
import com.coleji.neptune.Storable.Fields.{DateTimeDatabaseField, IntDatabaseField, NullableDoubleDatabaseField}
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.{DefinedInitializable, Initializable}
import org.sailcbi.APIServer.IO.CachedData


class JpClassSession extends StorableClass(JpClassSession) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
	}

	object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, JpClassSession.fields.sessionId)
		val instanceId = new IntFieldValue(self, JpClassSession.fields.instanceId)
		val sessionDateTime = new DateTimeFieldValue(self, JpClassSession.fields.sessionDateTime)
		val lengthOverride = new NullableDoubleFieldValue(self, JpClassSession.fields.lengthOverride)
	}

	object calculatedValues extends CalculatedValuesObject {
		val jpWeekAlias = new DefinedInitializable[UnlockedRequestCache, Option[String]]((rc: UnlockedRequestCache) => {
			val cache = new CachedData(rc)
			cache.getJpWeekAlias(values.sessionDateTime.get.toLocalDate)
		})
	}
}

object JpClassSession extends StorableObject[JpClassSession] {
	val entityName: String = "JP_CLASS_SESSIONS"

	object fields extends FieldsObject {
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
		val lengthOverride = new NullableDoubleDatabaseField(self, "LENGTH_OVERRIDE")
	}

	def primaryKey: IntDatabaseField = fields.sessionId
}