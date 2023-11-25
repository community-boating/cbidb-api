package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import org.sailcbi.APIServer.Entities.NullableInDatabase

class PersonsDiscountsEligible extends StorableClass(PersonsDiscountsEligible) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, PersonsDiscountsEligible.fields.rowId)
		val personId = new IntFieldValue(self, PersonsDiscountsEligible.fields.personId)
		val discountId = new IntFieldValue(self, PersonsDiscountsEligible.fields.discountId)
		val season = new IntFieldValue(self, PersonsDiscountsEligible.fields.season)
		val createdOn = new DateTimeFieldValue(self, PersonsDiscountsEligible.fields.createdOn)
		val createdBy = new StringFieldValue(self, PersonsDiscountsEligible.fields.createdBy)
		val updatedOn = new DateTimeFieldValue(self, PersonsDiscountsEligible.fields.updatedOn)
		val updatedBy = new StringFieldValue(self, PersonsDiscountsEligible.fields.updatedBy)
	}
}

object PersonsDiscountsEligible extends StorableObject[PersonsDiscountsEligible] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_DISCOUNTS_ELIGIBLE"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		@NullableInDatabase
		val personId = new IntDatabaseField(self, "PERSON_ID")
		@NullableInDatabase
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		@NullableInDatabase
		val season = new IntDatabaseField(self, "SEASON")
		@NullableInDatabase
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		@NullableInDatabase
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		@NullableInDatabase
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		@NullableInDatabase
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}