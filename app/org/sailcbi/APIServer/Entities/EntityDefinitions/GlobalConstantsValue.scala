package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GlobalConstantsValue extends StorableClass(GlobalConstantsValue) {
	override object values extends ValuesObject {
		val valueId = new IntFieldValue(self, GlobalConstantsValue.fields.valueId)
		val constantId = new IntFieldValue(self, GlobalConstantsValue.fields.constantId)
		val effectiveDatetime = new DateTimeFieldValue(self, GlobalConstantsValue.fields.effectiveDatetime)
		val createdOn = new NullableDateTimeFieldValue(self, GlobalConstantsValue.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GlobalConstantsValue.fields.createdBy)
		val updatedOn = new NullableDateTimeFieldValue(self, GlobalConstantsValue.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GlobalConstantsValue.fields.updatedBy)
		val valueNumber = new NullableDoubleFieldValue(self, GlobalConstantsValue.fields.valueNumber)
		val valueDate = new NullableDateTimeFieldValue(self, GlobalConstantsValue.fields.valueDate)
	}
}

object GlobalConstantsValue extends StorableObject[GlobalConstantsValue] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "GLOBAL_CONSTANTS_VALUES"

	object fields extends FieldsObject {
		val valueId = new IntDatabaseField(self, "VALUE_ID")
		val constantId = new IntDatabaseField(self, "CONSTANT_ID")
		val effectiveDatetime = new DateTimeDatabaseField(self, "EFFECTIVE_DATETIME")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val valueNumber = new NullableDoubleDatabaseField(self, "VALUE_NUMBER")
		val valueDate = new NullableDateTimeDatabaseField(self, "VALUE_DATE")
	}

	def primaryKey: IntDatabaseField = fields.valueId
}