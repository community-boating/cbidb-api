package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{DateTimeFieldValue, IntFieldValue}
import com.coleji.framework.Storable.Fields.{DateTimeDatabaseField, IntDatabaseField}
import com.coleji.framework.Storable._
import com.coleji.framework.Util.Initializable

class JpClassStagger extends StorableClass(JpClassStagger) {
	override object references extends ReferencesObject {
		val jpClassInstance = new Initializable[JpClassInstance]
	}

	object values extends ValuesObject {
		val sessionId = new IntFieldValue(self, JpClassStagger.fields.staggerId)
		val instanceId = new IntFieldValue(self, JpClassStagger.fields.instanceId)
		val staggerDate = new DateTimeFieldValue(self, JpClassStagger.fields.staggerDate)
		val occupancy = new IntFieldValue(self, JpClassStagger.fields.occupancy)
	}
}

object JpClassStagger extends StorableObject[JpClassStagger] {
	override val entityName: String = "JP_CLASS_STAGGERS"

	object fields extends FieldsObject {
		val staggerId = new IntDatabaseField(self, "STAGGER_ID")
		val instanceId = new IntDatabaseField(self, "INSTANCE_ID")
		val staggerDate = new DateTimeDatabaseField(self, "STAGGER_DATE")
		val occupancy = new IntDatabaseField(self, "OCCUPANCY")
	}

	override def primaryKey: IntDatabaseField = fields.staggerId
}
