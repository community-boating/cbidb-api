package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{DoubleFieldValue, IntFieldValue, NullableIntFieldValue, StringFieldValue}
import com.coleji.framework.Storable.Fields.{DoubleDatabaseField, IntDatabaseField, NullableIntDatabaseField, StringDatabaseField}
import com.coleji.framework.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class JpClassType extends StorableClass(JpClassType) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, JpClassType.fields.typeId)
		val typeName = new StringFieldValue(self, JpClassType.fields.typeName)
		val displayOrder = new NullableIntFieldValue(self, JpClassType.fields.displayOrder)
		val sessionlength = new DoubleFieldValue(self, JpClassType.fields.sessionLength)
	}
}

object JpClassType extends StorableObject[JpClassType] {
	val entityName: String = "JP_CLASS_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		@NullableInDatabase
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 50)
		val displayOrder = new NullableIntDatabaseField(self, "DISPLAY_ORDER")
		@NullableInDatabase
		val sessionLength = new DoubleDatabaseField(self, "SESSION_LENGTH")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}