package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class IncomeLevel extends StorableClass(IncomeLevel) {
	override object values extends ValuesObject {
		val levelId = new IntFieldValue(self, IncomeLevel.fields.levelId)
		val levelFloor = new NullableDoubleFieldValue(self, IncomeLevel.fields.levelFloor)
		val levelCeiling = new NullableDoubleFieldValue(self, IncomeLevel.fields.levelCeiling)
		val createdOn = new DateTimeFieldValue(self, IncomeLevel.fields.createdOn)
		val createdBy = new StringFieldValue(self, IncomeLevel.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, IncomeLevel.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, IncomeLevel.fields.updatedBy)
	}
}

object IncomeLevel extends StorableObject[IncomeLevel] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "INCOME_LEVELS"

	object fields extends FieldsObject {
		val levelId = new IntDatabaseField(self, "LEVEL_ID")
		val levelFloor = new NullableDoubleDatabaseField(self, "LEVEL_FLOOR")
		val levelCeiling = new NullableDoubleDatabaseField(self, "LEVEL_CEILING")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.levelId
}