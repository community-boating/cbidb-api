package org.sailcbi.APIServer.Entities.EntityDefinitions

import com.coleji.neptune.Storable.FieldValues._
import com.coleji.neptune.Storable.Fields._
import com.coleji.neptune.Storable._
import com.coleji.neptune.Util.Initializable

class PendingMembership extends StorableClass(PendingMembership) {
	object values extends ValuesObject {
		val personId = new IntFieldValue(self, PendingMembership.fields.personId)
		val membershipTypeId = new NullableIntFieldValue(self, PendingMembership.fields.membershipTypeId)
		val firstPayment = new NullableLocalDateTimeFieldValue(self, PendingMembership.fields.firstPayment)
		val lastPayment = new NullableLocalDateTimeFieldValue(self, PendingMembership.fields.lastPayment)
		val orderId = new NullableIntFieldValue(self, PendingMembership.fields.orderId)
	}
}

object PendingMembership extends StorableObject[PendingMembership] {
	val entityName: String = "PENDING_MEMBERSHIPS"

	object fields extends FieldsObject {
		val personId = new IntDatabaseField(self, "PERSON_ID")
		val membershipTypeId = new NullableIntDatabaseField(self, "MEMBERSHIP_TYPE_ID")
		val firstPayment = new NullableLocalDateTimeDatabaseField(self, "FIRST_PAYMENT")
		val lastPayment = new NullableLocalDateTimeDatabaseField(self, "LAST_PAYMENT")
		val orderId = new NullableIntDatabaseField(self, "ORDER_ID")
	}
}