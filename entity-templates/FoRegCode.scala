package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoRegCode extends StorableClass(FoRegCode) {
	object values extends ValuesObject {
		val code = new NullableDoubleFieldValue(self, FoRegCode.fields.code)
		val description = new NullableStringFieldValue(self, FoRegCode.fields.description)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoRegCode.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoRegCode.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoRegCode.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoRegCode.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, FoRegCode.fields.active)
		val rowId = new IntFieldValue(self, FoRegCode.fields.rowId)
	}
}

object FoRegCode extends StorableObject[FoRegCode] {
	val entityName: String = "FO_REG_CODES"

	object fields extends FieldsObject {
		val code = new NullableDoubleDatabaseField(self, "CODE")
		val description = new NullableStringDatabaseField(self, "DESCRIPTION", 200)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val rowId = new IntDatabaseField(self, "ROW_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}