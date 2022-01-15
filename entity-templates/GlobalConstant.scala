package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GlobalConstant extends StorableClass(GlobalConstant) {
	object values extends ValuesObject {
		val constantId = new IntFieldValue(self, GlobalConstant.fields.constantId)
		val constantName = new StringFieldValue(self, GlobalConstant.fields.constantName)
		val dataType = new DoubleFieldValue(self, GlobalConstant.fields.dataType)
		val createdOn = new NullableLocalDateTimeFieldValue(self, GlobalConstant.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, GlobalConstant.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, GlobalConstant.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, GlobalConstant.fields.updatedBy)
	}
}

object GlobalConstant extends StorableObject[GlobalConstant] {
	val entityName: String = "GLOBAL_CONSTANTS"

	object fields extends FieldsObject {
		val constantId = new IntDatabaseField(self, "CONSTANT_ID")
		val constantName = new StringDatabaseField(self, "CONSTANT_NAME", 100)
		val dataType = new DoubleDatabaseField(self, "DATA_TYPE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.constantId
}