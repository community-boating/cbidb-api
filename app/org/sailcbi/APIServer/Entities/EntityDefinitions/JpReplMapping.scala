package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class JpReplMapping extends StorableClass(JpReplMapping) {
	override object values extends ValuesObject {
		val mapId = new IntFieldValue(self, JpReplMapping.fields.mapId)
		val originalNum = new NullableStringFieldValue(self, JpReplMapping.fields.originalNum)
		val replNum = new NullableStringFieldValue(self, JpReplMapping.fields.replNum)
		val createdOn = new DateTimeFieldValue(self, JpReplMapping.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpReplMapping.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, JpReplMapping.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpReplMapping.fields.updatedBy)
	}
}

object JpReplMapping extends StorableObject[JpReplMapping] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "JP_REPL_MAPPING"

	object fields extends FieldsObject {
		val mapId = new IntDatabaseField(self, "MAP_ID")
		val originalNum = new NullableStringDatabaseField(self, "ORIGINAL_NUM", 15)
		val replNum = new NullableStringDatabaseField(self, "REPL_NUM", 15)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.mapId
}