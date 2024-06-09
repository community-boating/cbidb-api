package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class Restriction extends StorableClass(Restriction) {
	override object values extends ValuesObject {
		val restrictionId = new IntFieldValue(self, Restriction.fields.restrictionId)
		val restrictionText = new StringFieldValue(self, Restriction.fields.restrictionText)
		val createdOn = new DateTimeFieldValue(self, Restriction.fields.createdOn)
		val createdBy = new StringFieldValue(self, Restriction.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, Restriction.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, Restriction.fields.updatedBy)
	}
}

object Restriction extends StorableObject[Restriction] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "RESTRICTIONS"

	object fields extends FieldsObject {
		val restrictionId = new IntDatabaseField(self, "RESTRICTION_ID")
		@NullableInDatabase
		val restrictionText = new StringDatabaseField(self, "RESTRICTION_TEXT", 1000)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.restrictionId
}