package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class ErrorType extends StorableClass(ErrorType) {
	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, ErrorType.fields.typeId)
		val errorMessage = new NullableStringFieldValue(self, ErrorType.fields.errorMessage)
		val throwCount = new NullableIntFieldValue(self, ErrorType.fields.throwCount)
		val lastInstance = new NullableDateTimeFieldValue(self, ErrorType.fields.lastInstance)
	}
}

object ErrorType extends StorableObject[ErrorType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "ERROR_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val errorMessage = new NullableStringDatabaseField(self, "ERROR_MESSAGE", 4000)
		val throwCount = new NullableIntDatabaseField(self, "THROW_COUNT")
		val lastInstance = new NullableDateTimeDatabaseField(self, "LAST_INSTANCE")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}