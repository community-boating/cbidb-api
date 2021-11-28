package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FlagsGlobal extends StorableClass(FlagsGlobal) {
	object values extends ValuesObject {
		val flagId = new IntFieldValue(self, FlagsGlobal.fields.flagId)
		val globalId = new IntFieldValue(self, FlagsGlobal.fields.globalId)
		val globalValue = new NullableBooleanFIeldValue(self, FlagsGlobal.fields.globalValue)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FlagsGlobal.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FlagsGlobal.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FlagsGlobal.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FlagsGlobal.fields.updatedBy)
	}
}

object FlagsGlobal extends StorableObject[FlagsGlobal] {
	val entityName: String = "FLAGS_GLOBALS"

	object fields extends FieldsObject {
		val flagId = new IntDatabaseField(self, "FLAG_ID")
		val globalId = new IntDatabaseField(self, "GLOBAL_ID")
		val globalValue = new NullableBooleanDatabaseField(self, "GLOBAL_VALUE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.flagId
}