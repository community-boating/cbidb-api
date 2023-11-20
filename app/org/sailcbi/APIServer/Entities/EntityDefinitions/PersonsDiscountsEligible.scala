package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsDiscountsEligible extends StorableClass(PersonsDiscountsEligible) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, PersonsDiscountsEligible.fields.rowId)
		val personId = new IntFieldValue(self, PersonsDiscountsEligible.fields.personId)
		val discountId = new IntFieldValue(self, PersonsDiscountsEligible.fields.discountId)
		val season = new DoubleFieldValue(self, PersonsDiscountsEligible.fields.season)
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
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val discountId = new IntDatabaseField(self, "DISCOUNT_ID")
		val season = new DoubleDatabaseField(self, "SEASON")
		val createdOn = new DateTimeDatabaseField(self, "CREATED_ON")
		val createdBy = new StringDatabaseField(self, "CREATED_BY", 500)
		val updatedOn = new DateTimeDatabaseField(self, "UPDATED_ON")
		val updatedBy = new StringDatabaseField(self, "UPDATED_BY", 500)
	}

	def primaryKey: IntDatabaseField = fields.rowId
}