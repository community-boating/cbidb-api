package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DhGlobal extends StorableClass(DhGlobal) {
	override object values extends ValuesObject {
		val globalId = new IntFieldValue(self, DhGlobal.fields.globalId)
		val description = new StringFieldValue(self, DhGlobal.fields.description)
		val createdOn = new NullableDateTimeFieldValue(self, DhGlobal.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DhGlobal.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, DhGlobal.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DhGlobal.fields.updatedBy)
		val alias = new StringFieldValue(self, DhGlobal.fields.alias)
	}
}

object DhGlobal extends StorableObject[DhGlobal] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DH_GLOBALS"

	object fields extends FieldsObject {
		val globalId = new IntDatabaseField(self, "GLOBAL_ID")
		@NullableInDatabase
		val description = new StringDatabaseField(self, "DESCRIPTION", 4000)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		@NullableInDatabase
		val alias = new StringDatabaseField(self, "ALIAS", 20)
	}

	def primaryKey: IntDatabaseField = fields.globalId
}