package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class FoCash extends StorableClass(FoCash) {
	override object values extends ValuesObject {
		val cashId = new IntFieldValue(self, FoCash.fields.cashId)
		val closeId = new IntFieldValue(self, FoCash.fields.closeId)
		val locationIndex = new IntFieldValue(self, FoCash.fields.locationIndex)
		val value = new DoubleFieldValue(self, FoCash.fields.value)
		val createdOn = new DateTimeFieldValue(self, FoCash.fields.createdOn)
		val createdBy = new StringFieldValue(self, FoCash.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, FoCash.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, FoCash.fields.updatedBy)
		val denomIndex = new IntFieldValue(self, FoCash.fields.denomIndex)
	}
}

object FoCash extends StorableObject[FoCash] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "FO_CASH"

	object fields extends FieldsObject {
		val cashId = new IntDatabaseField(self, "CASH_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val locationIndex = new IntDatabaseField(self, "LOCATION_INDEX")
		val value = new DoubleDatabaseField(self, "VALUE")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val denomIndex = new IntDatabaseField(self, "DENOM_INDEX")
	}

	def primaryKey: IntDatabaseField = fields.cashId
}