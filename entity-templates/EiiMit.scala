package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EiiMit extends StorableClass(EiiMit) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, EiiMit.fields.rowId)
		val adults = new NullableDoubleFieldValue(self, EiiMit.fields.adults)
		val children = new NullableDoubleFieldValue(self, EiiMit.fields.children)
		val eii = new NullableDoubleFieldValue(self, EiiMit.fields.eii)
		val generation = new NullableDoubleFieldValue(self, EiiMit.fields.generation)
	}
}

object EiiMit extends StorableObject[EiiMit] {
	val entityName: String = "EII_MIT"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val adults = new NullableDoubleDatabaseField(self, "ADULTS")
		val children = new NullableDoubleDatabaseField(self, "CHILDREN")
		val eii = new NullableDoubleDatabaseField(self, "EII")
		val generation = new NullableDoubleDatabaseField(self, "GENERATION")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}