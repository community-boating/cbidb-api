package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DhGlobal extends StorableClass(DhGlobal) {
	object values extends ValuesObject {
		val globalId = new IntFieldValue(self, DhGlobal.fields.globalId)
		val description = new NullableStringFieldValue(self, DhGlobal.fields.description)
		val createdOn = new NullableLocalDateTimeFieldValue(self, DhGlobal.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DhGlobal.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, DhGlobal.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DhGlobal.fields.updatedBy)
		val alias = new NullableStringFieldValue(self, DhGlobal.fields.alias)
	}
}

object DhGlobal extends StorableObject[DhGlobal] {
	val entityName: String = "DH_GLOBALS"

	object fields extends FieldsObject {
		val globalId = new IntDatabaseField(self, "GLOBAL_ID")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 4000)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val alias = new NullableStringDatabaseField(self, "ALIAS", 20)
	}

	def primaryKey: IntDatabaseField = fields.globalId
}