package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class GlobalConstantDataType extends StorableClass(GlobalConstantDataType) {
	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, GlobalConstantDataType.fields.typeId)
		val typeName = new StringFieldValue(self, GlobalConstantDataType.fields.typeName)
	}
}

object GlobalConstantDataType extends StorableObject[GlobalConstantDataType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "GLOBAL_CONSTANT_DATA_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		@NullableInDatabase
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
	}

	def primaryKey: IntDatabaseField = fields.typeId
}