package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class EiiMit extends StorableClass(EiiMit) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, EiiMit.fields.rowId)
		val adults = new IntFieldValue(self, EiiMit.fields.adults)
		val children = new IntFieldValue(self, EiiMit.fields.children)
		val eii = new DoubleFieldValue(self, EiiMit.fields.eii)
		val generation = new IntFieldValue(self, EiiMit.fields.generation)
		val nonworkingAdults = new IntFieldValue(self, EiiMit.fields.nonworkingAdults)
	}
}

object EiiMit extends StorableObject[EiiMit] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "EII_MIT"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		@NullableInDatabase
		val adults = new IntDatabaseField(self, "ADULTS")
		@NullableInDatabase
		val children = new IntDatabaseField(self, "CHILDREN")
		@NullableInDatabase
		val eii = new DoubleDatabaseField(self, "EII")
		@NullableInDatabase
		val generation = new IntDatabaseField(self, "GENERATION")
		@NullableInDatabase
		val nonworkingAdults = new IntDatabaseField(self, "NONWORKING_ADULTS")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}