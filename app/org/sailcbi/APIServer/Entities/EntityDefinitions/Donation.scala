package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{DateFieldValue, IntFieldValue, NullableDoubleFieldValue}
import com.coleji.neptune.Storable.Fields.{DateDatabaseField, IntDatabaseField, NullableDoubleDatabaseField}
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable
import org.sailcbi.APIServer.Entities.NullableInDatabase

class Donation extends StorableClass(Donation) {
	override object references extends ReferencesObject {
		val person = new Initializable[Person]
	}

	object values extends ValuesObject {
		val donationId = new IntFieldValue(self, Donation.fields.donationId)
		val personId = new IntFieldValue(self, Donation.fields.personId)
		val amount = new NullableDoubleFieldValue(self, Donation.fields.amount)
		val donationDate = new DateFieldValue(self, Donation.fields.donationDate)
	}
}

object Donation extends StorableObject[Donation] {
	val entityName: String = "DONATIONS"

	object fields extends FieldsObject {
		val donationId = new IntDatabaseField(self, "DONATION_ID")
		@NullableInDatabase
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
		@NullableInDatabase
		val donationDate = new DateDatabaseField(self, "DONATION_DATE")
	}

	def primaryKey: IntDatabaseField = fields.donationId
}