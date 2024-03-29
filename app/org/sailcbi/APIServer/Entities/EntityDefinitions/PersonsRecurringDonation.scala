package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util._
import org.sailcbi.APIServer.Entities.NullableInDatabase
import org.sailcbi.APIServer.Entities.entitycalculations._
import play.api.libs.json._

class PersonsRecurringDonation extends StorableClass(PersonsRecurringDonation) {
	override object values extends ValuesObject {
		val rowId = new IntFieldValue(self, PersonsRecurringDonation.fields.rowId)
		val personId = new IntFieldValue(self, PersonsRecurringDonation.fields.personId)
		val fundId = new IntFieldValue(self, PersonsRecurringDonation.fields.fundId)
		val amountInCents = new IntFieldValue(self, PersonsRecurringDonation.fields.amountInCents)
		val createdOn = new NullableDateTimeFieldValue(self, PersonsRecurringDonation.fields.createdOn)
		val embryonic = new NullableBooleanFieldValue(self, PersonsRecurringDonation.fields.embryonic)
	}
}

object PersonsRecurringDonation extends StorableObject[PersonsRecurringDonation] {
	override val useRuntimeFieldnamesForJson: Boolean = true

	override val entityName: String = "PERSONS_RECURRING_DONATIONS"

	object fields extends FieldsObject {
		val rowId = new IntDatabaseField(self, "ROW_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val fundId = new IntDatabaseField(self, "FUND_ID")
		val amountInCents = new IntDatabaseField(self, "AMOUNT_IN_CENTS")
		val createdOn = new NullableDateTimeDatabaseField(self, "CREATED_ON")
		val embryonic = new NullableBooleanDatabaseField(self, "EMBRYONIC")
	}

	def primaryKey: IntDatabaseField = fields.rowId
}