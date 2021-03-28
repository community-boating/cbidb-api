package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Core.RequestCache
import com.coleji.framework.Storable.FieldValues.{DateTimeFieldValue, IntFieldValue}
import com.coleji.framework.Storable.Fields.{DateTimeDatabaseField, IntDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.{DefinedInitializable, Initializable}


class JpClassSession extends StorableClass {
	val myself = this
	this.setCompanion(JpClassSession)

	object references extends ReferencesObject {
		var jpClassInstance = new Initializable[JpClassInstance]
	}

	object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, JpClassSession.fields.sessionId)
		val instanceId = new IntFieldValue(self, JpClassSession.fields.instanceId)
		val sessionDateTime = new DateTimeFieldValue(self, JpClassSession.fields.sessionDateTime)
	}

	override val valuesList = List(
		values.sessionId,
		values.instanceId,
		values.sessionDateTime
	)

	object calculatedValues extends CalculatedValuesObject {
		val jpWeekAlias = new DefinedInitializable[RequestCache, Option[String]]((rc: RequestCache) => {
			rc.logic.dateLogic.getJpWeekAlias(myself.values.sessionDateTime.get.toLocalDate)
		})
	}
}

object JpClassSession extends StorableObject[JpClassSession] {
	val entityName: String = "JP_CLASS_SESSIONS"

	object fields extends FieldsObject {
		val sessionId = new IntDatabaseField(self, "SESSION_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val sessionDateTime = new DateTimeDatabaseField(self, "SESSION_DATETIME")
	}

	def primaryKey: IntDatabaseField = fields.sessionId
}