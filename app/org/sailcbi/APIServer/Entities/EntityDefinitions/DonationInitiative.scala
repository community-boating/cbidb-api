package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class DonationInitiative extends StorableClass(DonationInitiative) {
	override object values extends ValuesObject {
		val initiativeId = new IntFieldValue(self, DonationInitiative.fields.initiativeId)
		val initiativeName = new StringFieldValue(self, DonationInitiative.fields.initiativeName)
		val createdOn = new DateTimeFieldValue(self, DonationInitiative.fields.createdOn)
		val createdBy = new StringFieldValue(self, DonationInitiative.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, DonationInitiative.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, DonationInitiative.fields.updatedBy)
		val active = new NullableBooleanFieldValue(self, DonationInitiative.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, DonationInitiative.fields.displayOrder)
	}
}

object DonationInitiative extends StorableObject[DonationInitiative] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "DONATION_INITIATIVES"

	object fields extends FieldsObject {
		val initiativeId = new IntDatabaseField(self, "INITIATIVE_ID")
		@NullableInDatabase
		val initiativeName = new StringDatabaseField(self, "INITIATIVE_NAME", 200)
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.initiativeId
}