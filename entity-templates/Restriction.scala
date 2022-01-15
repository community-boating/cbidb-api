package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class Restriction extends StorableClass(Restriction) {
	object values extends ValuesObject {
		val restrictionId = new IntFieldValue(self, Restriction.fields.restrictionId)
		val restrictionText = new NullableStringFieldValue(self, Restriction.fields.restrictionText)
		val createdOn = new NullableLocalDateTimeFieldValue(self, Restriction.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, Restriction.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, Restriction.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, Restriction.fields.updatedBy)
	}
}

object Restriction extends StorableObject[Restriction] {
	val entityName: String = "RESTRICTIONS"

	object fields extends FieldsObject {
		val restrictionId = new IntDatabaseField(self, "RESTRICTION_ID")
		val restrictionText = new NullableStringDatabaseField(self, "RESTRICTION_TEXT", 1000)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.restrictionId
}