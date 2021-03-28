package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.framework.Storable.FieldValues.{DateFieldValue, IntFieldValue, NullableDoubleFieldValue}
import com.coleji.framework.Storable.Fields.{DateDatabaseField, IntDatabaseField, NullableDoubleDatabaseField}
import com.coleji.framework.Storable._
import org.sailcbi.APIServer.CbiUtil.Initializable

class Donation extends StorableClass {
	this.setCompanion(Donation)

	object references extends ReferencesObject {
		var person = new Initializable[Person]
	}

	object values extends ValuesObject {
		val donationId = new IntFieldValue(self, Donation.fields.donationId)
		val personId = new IntFieldValue(self, Donation.fields.personId)
		val amount = new NullableDoubleFieldValue(self, Donation.fields.amount)
		val donationDate = new DateFieldValue(self, Donation.fields.donationDate)
	}

	override val valuesList = List(
		values.donationId,
		values.personId,
		values.amount,
		values.donationDate
	)
}

object Donation extends StorableObject[Donation] {
	val entityName: String = "DONATIONS"

	object fields extends FieldsObject {
		val donationId = new IntDatabaseField(self, "DONATION_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val amount = new NullableDoubleDatabaseField(self, "AMOUNT")
		val donationDate = new DateDatabaseField(self, "DONATION_DATE")
	}

	def primaryKey: IntDatabaseField = fields.donationId
}