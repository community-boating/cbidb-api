package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class GlobalConstantDataType extends StorableClass(GlobalConstantDataType) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, GlobalConstantDataType.fields.typeId)
		val typeName = new NullableStringFieldValue(self, GlobalConstantDataType.fields.typeName)
	}
}

object GlobalConstantDataType extends StorableObject[GlobalConstantDataType] {
	val entityName: String = "GLOBAL_CONSTANT_DATA_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new NullableStringDatabaseField(self, "TYPE_NAME", 50)
	}

	def primaryKey: IntDatabaseField = fields.typeId
}