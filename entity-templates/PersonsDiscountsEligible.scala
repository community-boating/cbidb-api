package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsDiscountsEligible extends StorableClass(PersonsDiscountsEligible) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, PersonsDiscountsEligible.fields.rowId)
		val personId = new NullableIntFieldValue(self, PersonsDiscountsEligible.fields.personId)
		val discountId = new NullableIntFieldValue(self, PersonsDiscountsEligible.fields.discountId)
		val season = new NullableDoubleFieldValue(self, PersonsDiscountsEligible.fields.season)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsDiscountsEligible.fields.createdOn)
		val createdBy = new NullableStringFieldValue(self, PersonsDiscountsEligible.fields.createdBy)
		val updatedOn = new NullableLocalDateTimeFieldValue(self, PersonsDiscountsEligible.fields.updatedOn)
		val updatedBy = new NullableStringFieldValue(self, PersonsDiscountsEligible.fields.updatedBy)
	}
}

object PersonsDiscountsEligible extends StorableObject[PersonsDiscountsEligible] {
	val entityName: String = "PERSONS_DISCOUNTS_ELIGIBLE"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new NullableIntDatabaseField(self, "PERSON_ID")
		val discountId = new NullableIntDatabaseField(self, "DISCOUNT_ID")
		val season = new NullableDoubleDatabaseField(self, "SEASON")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new NullableStringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new NullableLocalDateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new NullableStringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}