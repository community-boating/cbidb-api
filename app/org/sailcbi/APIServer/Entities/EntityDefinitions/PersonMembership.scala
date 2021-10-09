package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues.{IntFieldValue, NullableDateFieldValue}
import com.coleji.neptune.Storable.Fields.{IntDatabaseField, NullableDateDatabaseField}
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PersonMembership extends StorableClass(PersonMembership) {
	override object references extends ReferencesObject {
		val person: Option[Person] = None
		val membershipType = new Initializable[MembershipType]
	}

	object values extends ValuesObject {
		val assignId = new IntFieldValue(self, PersonMembership.fields.assignId)
		val personId = new IntFieldValue(self, PersonMembership.fields.personId)
		val membershipTypeId = new IntFieldValue(self, PersonMembership.fields.membershipTypeId)
		val startDate = new NullableDateFieldValue(self, PersonMembership.fields.startDate)
		val expirationDate = new NullableDateFieldValue(self, PersonMembership.fields.expirationDate)
	}
}

object PersonMembership extends StorableObject[PersonMembership] {
	val entityName: String = "PERSONS_MEMBERSHIPS"

	object fields extends FieldsObject {
		val assignId = new IntDatabaseField(self, "ASSIGN_ID")
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val membershipTypeId = new IntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val startDate = new NullableDateDatabaseField(self, "START_DATE")
		val expirationDate = new NullableDateDatabaseField(self, "EXPIRATION_DATE")
	}

	def primaryKey: IntDatabaseField = fields.assignId
}