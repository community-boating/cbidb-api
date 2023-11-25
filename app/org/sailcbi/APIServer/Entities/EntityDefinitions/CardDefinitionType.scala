package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class CardDefinitionType extends StorableClass(CardDefinitionType) {
	override object values extends ValuesObject {
		val typeId = new IntFieldValue(self, CardDefinitionType.fields.typeId)
		val typeName = new StringFieldValue(self, CardDefinitionType.fields.typeName)
		val warnAt = new NullableIntFieldValue(self, CardDefinitionType.fields.warnAt)
	}
}

object CardDefinitionType extends StorableObject[CardDefinitionType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CARD_DEFINITION_TYPES"

	object fields extends FieldsObject {
		val typeId = new IntDatabaseField(self, "TYPE_ID")
		@NullableInDatabase
		val typeName = new StringDatabaseField(self, "TYPE_NAME", 100)
		val warnAt = new NullableIntDatabaseField(self, "WARN_AT")
	}

	def primaryKey: IntDatabaseField = fields.typeId
}