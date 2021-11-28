package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CardDefinitionType extends StorableClass(CardDefinitionType) {
	object values extends ValuesObject {
		val typeId = new IntFieldValue(self, CardDefinitionType.fields.typeId)
		val typeName = new NullableStringFieldValue(self, CardDefinitionType.fields.typeName)
		val warnAt = new NullableDoubleFieldValue(self, CardDefinitionType.fields.warnAt)
	}
}

object CardDefinitionType extends StorableObject[CardDefinitionType] {
	val entityName: String = "CARD_DEFINITION_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		val typeName = new NullableStringDatabaseField(self, "TYPE_NAME", 100)
		val warnAt = new NullableDoubleDatabaseField(self, "WARN_AT")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}