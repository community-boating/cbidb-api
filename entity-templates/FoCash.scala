package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class FoCash extends StorableClass(FoCash) {
	object values extends ValuesObject {
		val cashId = new IntFieldValue(self, FoCash.fields.cashId)
		val closeId = new IntFieldValue(self, FoCash.fields.closeId)
		val locationIndex = new DoubleFieldValue(self, FoCash.fields.locationIndex)
		val value = new DoubleFieldValue(self, FoCash.fields.value)
		val createdOn = new NullableLocalDateTimeFieldValue(self, FoCash.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, FoCash.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, FoCash.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, FoCash.fields.updatedBy)
		val denomIndex = new DoubleFieldValue(self, FoCash.fields.denomIndex)
	}
}

object FoCash extends StorableObject[FoCash] {
	val entityName: String = "FO_CASH"

	object fields extends FieldsObject {
		val cashId = new IntDatabaseField(self, "CASH_ID")
		val closeId = new IntDatabaseField(self, "CLOSE_ID")
		val locationIndex = new DoubleDatabaseField(self, "LOCATION_INDEX")
		val value = new DoubleDatabaseField(self, "VALUE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val denomIndex = new DoubleDatabaseField(self, "DENOM_INDEX")
	}

	def primaryKey: IntDatabaseField = fields.cashId
}