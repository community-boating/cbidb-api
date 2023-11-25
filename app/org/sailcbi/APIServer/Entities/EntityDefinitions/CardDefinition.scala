package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class CardDefinition extends StorableClass(CardDefinition) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, CardDefinition.fields.rowId)
		val cardNumber = new StringFieldValue(self, CardDefinition.fields.cardNumber)
		val programId = new NullableIntFieldValue(self, CardDefinition.fields.programId)
		val comments = new NullableStringFieldValue(self, CardDefinition.fields.comments)
		val typeId = new IntFieldValue(self, CardDefinition.fields.typeId)
	}
}

object CardDefinition extends StorableObject[CardDefinition] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "CARD_DEFINITIONS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		@NullableInDatabase
		val cardNumber = new StringDatabaseField(self, "CARD_NUMBER", 20)
		val programId = new NullableIntDatabaseField(self, "PROGRAM_ID")
		val comments = new NullableStringDatabaseField(self, "COMMENTS", 200)
		@NullableInDatabase
		val typeId = new IntDatabaseField(self, "TYPE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}