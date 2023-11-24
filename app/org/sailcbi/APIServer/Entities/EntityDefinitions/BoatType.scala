package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class BoatType extends StorableClass(BoatType) {
	override object values extends ValuesObject {
		val boatId = new IntFieldValue(self, BoatType.fields.boatId)
		val boatName = new StringFieldValue(self, BoatType.fields.boatName)
		val active = new NullableBooleanFieldValue(self, BoatType.fields.active)
		val displayOrder = new DoubleFieldValue(self, BoatType.fields.displayOrder)
		val createdOn = new DateTimeFieldValue(self, BoatType.fields.createdOn)
		val createdBy = new StringFieldValue(self, BoatType.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, BoatType.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, BoatType.fields.updatedBy)
		val minCrew = new DoubleFieldValue(self, BoatType.fields.minCrew)
		val maxCrew = new DoubleFieldValue(self, BoatType.fields.maxCrew)
		val imageFilename = new NullableStringFieldValue(self, BoatType.fields.imageFilename)
	}
}

object BoatType extends StorableObject[BoatType] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "BOAT_TYPES"

	object fields extends FieldsObject {
		val boatId = new IntDatabaseField(self, "BOAT_ID")
		@NullableInDatabase
		val boatName = new StringDatabaseField(self, "BOAT_NAME", 50)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		@NullableInDatabase
		val displayOrder = new DoubleDatabaseField(self, "DISPLAY_ORDER")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		@NullableInDatabase
		val minCrew = new DoubleDatabaseField(self, "MIN_CREW")
		@NullableInDatabase
		val maxCrew = new DoubleDatabaseField(self, "MAX_CREW")
		val imageFilename = new NullableStringDatabaseField(self, "IMAGE_FILENAME", 50)
	}

	def primaryKey: IntDatabaseField = fields.boatId
}