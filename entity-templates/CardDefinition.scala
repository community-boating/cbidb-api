package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class CardDefinition extends StorableClass(CardDefinition) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, CardDefinition.fields.rowId)
		val cardNumber = new NullableStringFieldValue(self, CardDefinition.fields.cardNumber)
		val programId = new NullableIntFieldValue(self, CardDefinition.fields.programId)
		val comments = new NullableStringFieldValue(self, CardDefinition.fields.comments)
		val typeId = new NullableIntFieldValue(self, CardDefinition.fields.typeId)
	}
}

object CardDefinition extends StorableObject[CardDefinition] {
	val entityName: String = "CARD_DEFINITIONS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val cardNumber = new NullableStringDatabaseField(self, "CARD_NUMBER", 20)
		val programId = new NullableIntDatabaseField(self, "PROGRAM_ID")
		val comments = new NullableStringDatabaseField(self, "COMMENTS", 200)
		val typeId = new NullableIntDatabaseField(self, "TYPE_ID")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}