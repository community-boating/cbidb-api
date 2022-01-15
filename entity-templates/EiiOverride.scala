package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class EiiOverride extends StorableClass(EiiOverride) {
	object values extends ValuesObject {
		val personId = new NullableIntFieldValue(self, EiiOverride.fields.personId)
		val season = new NullableDoubleFieldValue(self, EiiOverride.fields.season)
		val price = new NullableDoubleFieldValue(self, EiiOverride.fields.price)
		val createdOn = new NullableLocalDateTimeFieldValue(self, EiiOverride.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, EiiOverride.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, EiiOverride.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, EiiOverride.fields.updatedBy)
		val overrideId = new IntFieldValue(self, EiiOverride.fields.overrideId)
	}
}

object EiiOverride extends StorableObject[EiiOverride] {
	val entityName: String = "EII_OVERRIDES"

	object fields extends FieldsObject {
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val season = new NullableDoubleDatabaseField(self, "SEASON")
		val price = new NullableDoubleDatabaseField(self, "PRICE")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val overrideId = new IntDatabaseField(self, "OVERRIDE_ID")
	}

	def primaryKey: IntDatabaseField = fields.overrideId
}