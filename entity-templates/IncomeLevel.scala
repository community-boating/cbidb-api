package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class IncomeLevel extends StorableClass(IncomeLevel) {
	object values extends ValuesObject {
		val levelId = new IntFieldValue(self, IncomeLevel.fields.levelId)
		val levelFloor = new NullableDoubleFieldValue(self, IncomeLevel.fields.levelFloor)
		val levelCeiling = new NullableDoubleFieldValue(self, IncomeLevel.fields.levelCeiling)
		val createdOn = new NullableLocalDateTimeFieldValue(self, IncomeLevel.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, IncomeLevel.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, IncomeLevel.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, IncomeLevel.fields.updatedBy)
	}
}

object IncomeLevel extends StorableObject[IncomeLevel] {
	val entityName: String = "INCOME_LEVELS"

	object fields extends FieldsObject {
		val levelId = new IntDatabaseField(self, "LEVEL_ID")
		val levelFloor = new NullableDoubleDatabaseField(self, "LEVEL_FLOOR")
		val levelCeiling = new NullableDoubleDatabaseField(self, "LEVEL_CEILING")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.levelId
}