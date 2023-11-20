package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FlagsGlobal extends StorableClass(FlagsGlobal) {
	override object values extends ValuesObject {
		val flagId = new IntFieldValue(self, FlagsGlobal.fields.flagId)
		val globalId = new IntFieldValue(self, FlagsGlobal.fields.globalId)
		val globalValue = new StringFieldValue(self, FlagsGlobal.fields.globalValue)
		val createdOn = new NullableDateTimeFieldValue(self, FlagsGlobal.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FlagsGlobal.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, FlagsGlobal.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FlagsGlobal.fields.updatedBy)
	}
}

object FlagsGlobal extends StorableObject[FlagsGlobal] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FLAGS_GLOBALS"

	object fields extends FieldsObject {
		val flagId = new IntDatabaseField(self, "FLAG_ID")
		val globalId = new IntDatabaseField(self, "GLOBAL_ID")
		val globalValue = new StringDatabaseField(self, "GLOBAL_VALUE", 1)
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.flagId
}