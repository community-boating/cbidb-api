package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class ErrorType extends StorableClass(ErrorType) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, ErrorType.fields.typeId)
		val errorMessage = new NullableStringFieldValue(self, ErrorType.fields.errorMessage)
		val throwCount = new NullableDoubleFieldValue(self, ErrorType.fields.throwCount)
		val lastInstance = new NullableLocalDateTimeFieldValue(self, ErrorType.fields.lastInstance)
	}
}

object ErrorType extends StorableObject[ErrorType] {
	val entityName: String = "ERROR_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val errorMessage = new NullableStringDatabaseField(self, "ERROR_MESSAGE", 4000)
		val throwCount = new NullableDoubleDatabaseField(self, "THROW_COUNT")
		val lastInstance = new NullableLocalDateTimeDatabaseField(self, "LAST_INSTANCE")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}