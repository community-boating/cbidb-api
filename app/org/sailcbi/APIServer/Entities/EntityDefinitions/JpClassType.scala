package org.sailcbi.APIServer.Entities.EntityDefinitions

import org.sailcbi.APIServer.Storable.FieldValues.{IntFieldValue, NullableIntFieldValue, StringFieldValue}
import org.sailcbi.APIServer.Storable.Fields.{IntDatabaseField, NullableIntDatabaseField, StringDatabaseField}
import org.sailcbi.APIServer.Storable._

class JpClassType extends StorableClass {
	this.setCompanion(JpClassType)

	object references extends ReferencesObject {}

	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, JpClassType.fields.typeId)
		val typeName = new StringFieldValue(self, JpClassType.fields.typeName)
		val displayOrder = new NullableIntFieldValue(self, JpClassType.fields.displayOrder)
	}

	override val valuesList = List(
		values.typeId,
		values.typeName,
		values.displayOrder
	)
}

object JpClassType extends StorableObject[JpClassType] {
	val entityName: String = "JP_CLASS_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
		val displayOrder = new NullableIntDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}