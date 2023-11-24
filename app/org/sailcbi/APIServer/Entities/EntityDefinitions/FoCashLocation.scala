package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class FoCashLocation extends StorableClass(FoCashLocation) {
	override object values extends ValuesObject {
		val locationIndex = new IntFieldValue(self, FoCashLocation.fields.locationIndex)
		val locationNameShort = new StringFieldValue(self, FoCashLocation.fields.locationNameShort)
		val locationNameLong = new StringFieldValue(self, FoCashLocation.fields.locationNameLong)
		val open = new BooleanFieldValue(self, FoCashLocation.fields.open)
	}
}

object FoCashLocation extends StorableObject[FoCashLocation] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_CASH_LOCATIONS"

	object fields extends FieldsObject {
		val locationIndex = new IntDatabaseField(self, "LOCATION_INDEX")
		val locationNameShort = new StringDatabaseField(self, "LOCATION_NAME_SHORT", 5)
		@NullableInDatabase
		val locationNameLong = new StringDatabaseField(self, "LOCATION_NAME_LONG", 100)
		val open = new BooleanDatabaseField(self, "OPEN", false)
	}

	def primaryKey: IntDatabaseField = fields.locationIndex
}