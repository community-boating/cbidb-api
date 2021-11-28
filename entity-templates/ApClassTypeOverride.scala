package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ApClassTypeOverride extends StorableClass(ApClassTypeOverride) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, ApClassTypeOverride.fields.rowId)
		val personId = new IntFieldValue(self, ApClassTypeOverride.fields.personId)
		val typeId = new IntFieldValue(self, ApClassTypeOverride.fields.typeId)
		val season = new DoubleFieldValue(self, ApClassTypeOverride.fields.season)
		val createdOn = new NullableLocalDateTimeFieldValue(self, ApClassTypeOverride.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, ApClassTypeOverride.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, ApClassTypeOverride.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, ApClassTypeOverride.fields.updatedBy)
		val isForOverkill = new BooleanFIeldValue(self, ApClassTypeOverride.fields.isForOverkill)
	}
}

object ApClassTypeOverride extends StorableObject[ApClassTypeOverride] {
	val entityName: String = "AP_CLASS_TYPE_OVERRIDES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val isForOverkill = new BooleanDatabaseField(self, "IS_FOR_OVERKILL")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}