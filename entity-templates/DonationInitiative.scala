package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class DonationInitiative extends StorableClass(DonationInitiative) {
	object values extends ValuesObject {
		val initiativeId = new IntFieldValue(self, DonationInitiative.fields.initiativeId)
		val initiativeName = new NullableStringFieldValue(self, DonationInitiative.fields.initiativeName)
		val createdOn = new NullableLocalDateTimeFieldValue(self, DonationInitiative.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, DonationInitiative.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, DonationInitiative.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, DonationInitiative.fields.updatedBy)
		val active = new NullableBooleanFIeldValue(self, DonationInitiative.fields.active)
		val displayOrder = new NullableDoubleFieldValue(self, DonationInitiative.fields.displayOrder)
	}
}

object DonationInitiative extends StorableObject[DonationInitiative] {
	val entityName: String = "DONATION_INITIATIVES"

	object fields extends FieldsObject {
		val initiativeId = new IntDatabaseField(self, "INITIATIVE_ID")
		val initiativeName = new NullableStringDatabaseField(self, "INITIATIVE_NAME", 200)
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
		val active = new NullableBooleanDatabaseField(self, "ACTIVE")
		val displayOrder = new NullableDoubleDatabaseField(self, "DISPLAY_ORDER")
	}

	def primaryKey: IntDatabaseField = fields.initiativeId
}