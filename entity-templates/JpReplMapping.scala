package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpReplMapping extends StorableClass(JpReplMapping) {
	object values extends ValuesObject {
		val mapId = new IntFieldValue(self, JpReplMapping.fields.mapId)
		val originalNum = new NullableStringFieldValue(self, JpReplMapping.fields.originalNum)
		val replNum = new NullableStringFieldValue(self, JpReplMapping.fields.replNum)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpReplMapping.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpReplMapping.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpReplMapping.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpReplMapping.fields.updatedBy)
	}
}

object JpReplMapping extends StorableObject[JpReplMapping] {
	val entityName: String = "JP_REPL_MAPPING"

	object fields extends FieldsObject {
		val mapId = new IntDatabaseField(self, "MAP_ID")
		val originalNum = new NullableStringDatabaseField(self, "ORIGINAL_NUM", 15)
		val replNum = new NullableStringDatabaseField(self, "REPL_NUM", 15)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.mapId
}