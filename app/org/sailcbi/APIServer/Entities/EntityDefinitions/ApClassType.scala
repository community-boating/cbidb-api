package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{IntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{IntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._

class ApClassType extends StorableClass(ApClassType) {
	object references extends ReferencesObject {}

	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, ApClassType.fields.typeId)
		val typeName = new StringFieldValue(self, ApClassType.fields.typeName)
		val displayOrder = new IntFieldValue(self, ApClassType.fields.displayOrder)
	}

	override val valuesList = List(
		values.typeId,
		values.typeName,
		values.displayOrder
	)
}

object ApClassType extends StorableObject[ApClassType] {
	val entityName: String = "AP_CLASS_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
		val displayOrder = new IntDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}