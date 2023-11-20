package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EiiMit extends StorableClass(EiiMit) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, EiiMit.fields.rowId)
		val adults = new DoubleFieldValue(self, EiiMit.fields.adults)
		val children = new DoubleFieldValue(self, EiiMit.fields.children)
		val eii = new DoubleFieldValue(self, EiiMit.fields.eii)
		val generation = new DoubleFieldValue(self, EiiMit.fields.generation)
		val nonworkingAdults = new DoubleFieldValue(self, EiiMit.fields.nonworkingAdults)
	}
}

object EiiMit extends StorableObject[EiiMit] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EII_MIT"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val adults = new DoubleDatabaseField(self, "ADULTS")
		val children = new DoubleDatabaseField(self, "CHILDREN")
		val eii = new DoubleDatabaseField(self, "EII")
		val generation = new DoubleDatabaseField(self, "GENERATION")
		val nonworkingAdults = new DoubleDatabaseField(self, "NONWORKING_ADULTS")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}