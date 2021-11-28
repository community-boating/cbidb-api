package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class JpClassTypeOverride extends StorableClass(JpClassTypeOverride) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, JpClassTypeOverride.fields.rowId)
		val personId = new IntFieldValue(self, JpClassTypeOverride.fields.personId)
		val typeId = new IntFieldValue(self, JpClassTypeOverride.fields.typeId)
		val season = new NullableDoubleFieldValue(self, JpClassTypeOverride.fields.season)
		val isForOverkill = new BooleanFIeldValue(self, JpClassTypeOverride.fields.isForOverkill)
		val createdOn = new NullableLocalDateTimeFieldValue(self, JpClassTypeOverride.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, JpClassTypeOverride.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, JpClassTypeOverride.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, JpClassTypeOverride.fields.updatedBy)
	}
}

object JpClassTypeOverride extends StorableObject[JpClassTypeOverride] {
	val entityName: String = "JP_CLASS_TYPE_OVERRIDES"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val season = new NullableDoubleDatabaseField(self, "SEASON")
		val isForOverkill = new BooleanDatabaseField(self, "IS_FOR_OVERKILL")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}