package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoCashLocation extends StorableClass(FoCashLocation) {
	object values extends ValuesObject {
		val locationIndex = new DoubleFieldValue(self, FoCashLocation.fields.locationIndex)
		val locationNameShort = new StringFieldValue(self, FoCashLocation.fields.locationNameShort)
		val locationNameLong = new NullableStringFieldValue(self, FoCashLocation.fields.locationNameLong)
		val open = new BooleanFIeldValue(self, FoCashLocation.fields.open)
	}
}

object FoCashLocation extends StorableObject[FoCashLocation] {
	val entityName: String = "FO_CASH_LOCATIONS"

	object fields extends FieldsObject {
		val locationIndex = new DoubleDatabaseField(self, "LOCATION_INDEX")
		val locationNameShort = new StringDatabaseField(self, "LOCATION_NAME_SHORT", 5)
		val locationNameLong = new NullableStringDatabaseField(self, "LOCATION_NAME_LONG", 100)
		val open = new BooleanDatabaseField(self, "OPEN")
	}

	def primaryKey: IntDatabaseField = fields.locationIndex
}