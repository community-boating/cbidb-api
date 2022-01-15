package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonsRecurringDonation extends StorableClass(PersonsRecurringDonation) {
	object values extends ValuesObject {
		val rowId = new IntFieldValue(self, PersonsRecurringDonation.fields.rowId)
		val personId = new IntFieldValue(self, PersonsRecurringDonation.fields.personId)
		val fundId = new IntFieldValue(self, PersonsRecurringDonation.fields.fundId)
		val amountInCents = new DoubleFieldValue(self, PersonsRecurringDonation.fields.amountInCents)
		val createdOn = new NullableLocalDateTimeFieldValue(self, PersonsRecurringDonation.fields.createdOn)
		val embryonic = new NullableBooleanFIeldValue(self, PersonsRecurringDonation.fields.embryonic)
	}
}

object PersonsRecurringDonation extends StorableObject[PersonsRecurringDonation] {
	val entityName: String = "PERSONS_RECURRING_DONATIONS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val fundId = new IntDatabaseField(self, "FUND_ID")
		val amountInCents = new DoubleDatabaseField(self, "AMOUNT_IN_CENTS")
		val createdOn = new NullableLocalDateTimeDatabaseField(self, "CREATED_ON")
		val embryonic = new NullableBooleanDatabaseField(self, "EMBRYONIC")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}