package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._

class BoatType extends StorableClass(BoatType) {
	object values extends ValuesObject {
		val boatId = new IntFieldValue(self, BoatType.fields.boatId)
		val boatName = new NullableStringFieldValue(self, BoatType.fields.boatName)
		val active = new BooleanFieldValue(self, BoatType.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, BoatType.fields.displayOrder)
		val minCrew = new NullableIntFieldValue(self, BoatType.fields.minCrew)
		val maxCrew = new NullableIntFieldValue(self, BoatType.fields.maxCrew)
	}
}

object BoatType extends StorableObject[BoatType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	val entityName: String = "BOAT_TYPES"

	object fields extends FieldsObject {
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		val boatName = new NullableStringDatabaseField(self, "BOAT_NAME", 50)
		val active = new BooleanDatabaseField(self, "ACTIVE", true)
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
		val minCrew = new NullableIntDatabaseField(self, "MIN_CREW")
		val maxCrew = new NullableIntDatabaseField(self, "MAX_CREW")
	}

	def primaryKey: IntDatabaseField = fields.boatId
}