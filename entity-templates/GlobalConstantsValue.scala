package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GlobalConstantsValue extends StorableClass(GlobalConstantsValue) {
	object values extends ValuesObject {
		val valueId = new IntFieldValue(self, GlobalConstantsValue.fields.valueId)
		val constantId = new NullableIntFieldValue(self, GlobalConstantsValue.fields.constantId)
		val effectiveDatetime = new NullableLocalDateTimeFieldValue(self, GlobalConstantsValue.fields.effectiveDatetime)
		val createdOn = new NullableLocalDateTimeFieldValue(self, GlobalConstantsValue.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GlobalConstantsValue.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, GlobalConstantsValue.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GlobalConstantsValue.fields.updatedBy)
		val valueNumber = new NullableDoubleFieldValue(self, GlobalConstantsValue.fields.valueNumber)
		val valueDate = new NullableLocalDateTimeFieldValue(self, GlobalConstantsValue.fields.valueDate)
	}
}

object GlobalConstantsValue extends StorableObject[GlobalConstantsValue] {
	val entityName: String = "GLOBAL_CONSTANTS_VALUES"

	object fields extends FieldsObject {
		val valueId = new IntDatabaseField(self, "VALUE_ID")
		val constantId = new NullableIntDatabaseField(self, "CONSTANT_ID")
		val effectiveDatetime = new NullableLocalDateTimeDatabaseField(self, "EFFECTIVE_DATETIME")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val valueNumber = new NullableDoubleDatabaseField(self, "VALUE_NUMBER")
		val valueDate = new NullableLocalDateTimeDatabaseField(self, "VALUE_DATE")
	}

	def primaryKey: IntDatabaseField = fields.valueId
}